pragma solidity ^0.4.18;

/**
 * cky 
 * 앱인증정보 저장,조회,삭제 러프하게 구성
 * 
 */
contract appHashCheck2 {

	/**
	 * TODO: 앱 버전별 인증정보 처리를 위해 2차원으로 확장 필요 (2D mapping)
	 */
	mapping (string => string) appHashMap;
	
	/**
	 * 매핑으로 전체 사이즈 및 순회처리가 안되서 추가
	 */
	string[] appUniqIds;

	/**
	 * 앱인증정보 사이즈
	 */
	function getRegistedCount() public constant returns (uint256) {
	    return appUniqIds.length;
	}

    /**
	 * 앱인증정보 등록
	 */
	function regist(string appUniqId, string appHashValue) public returns (bool) {
	    require(bytes(appUniqId).length > 0);
	    require(bytes(appHashValue).length > 0);

	    bool isExist = exist(appUniqId);
	    appHashMap[appUniqId] = appHashValue;
	    
	    if( isExist == false ) {
	        appUniqIds.push(appUniqId);
	    }
	    return true;
	}

    /**
	 * 앱인증정보 조회
	 */
	function check(string appUniqId, string appHashValue) public constant returns (int8) {
	    require(bytes(appUniqId).length>0);
	    require(bytes(appHashValue).length>0);

	    if( bytes(appHashMap[appUniqId]).length == 0 )
	        return -2;
        else if( keccak256(appHashMap[appUniqId]) == keccak256(appHashValue) )
            return 0;
            
	    return -3;
	}
	
    /**
	 * 앱인증정보 삭제
	 * 원래 제공하면 안되는 기능이나 데모를 위해 추가함
	 */
	function remove(string appUniqId) public returns (bool) {
        require(bytes(appUniqId).length>0);

        if( exist(appUniqId) == true ) {
            delete appHashMap[appUniqId];
            
            for( uint i = 0 ; i < appUniqIds.length ; i++ ) {
                if( keccak256(appUniqId) == keccak256(appUniqIds[i]) ) {
                    
                    for (uint j = i; j < appUniqIds.length-1; j++ )
                        appUniqIds[j] = appUniqIds[j+1];
                    appUniqIds.length--;
                    break;
                }
            }
            return true;
        }
        return false;
	}

    /**
	 * 앱인증정보 확인용
	 */
	function getAppHashValue(string appUniqId) public view returns (string) {
	    require( bytes(appUniqId).length > 0 );
	    return appHashMap[appUniqId];
	}

    /**
	 * 앱인증정보 유무확인용
	 */
	function exist(string appUniqId) public constant returns (bool) {
	    require( bytes(appUniqId).length > 0 );

	    if( bytes(appHashMap[appUniqId]).length != 0 )
	        return true;
	        
        return false;
	}
	
	
}