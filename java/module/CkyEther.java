package com.kydsessc.ncm.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;
import android.util.Base64;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Int8;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.AdminFactory;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/*
 로컬에 사설네트워크를 구축하여 실행
 데모용 모듈

 * Created by KiYeon Cho / Shinhan DS
 * web3j관련 처리만 참조하세요 

 - 데모를 위해 급히 러프하게 제작한 모듈이므로
   적절한 테스트와 추가 정합성체크가 필요함
 */
public final class CkyEther {

    private CkyEther sInstance;

    private Web3j mWeb3j;

    public static String mPrivateGethUrl;

    private String mPassCode;

    private String mMainAddr;

    private String mContractAddr;

    private boolean mIsSetupCompleted;

    private boolean mIsPrepared;

    private String mAppHashValue;

    private String mCertSHA1Fingerprint;

    private String mCertSigHashValue;

    private String mAppUniqId;

    private long mGasPrice, mGasLimit;


    public static CkyEther sharedInstance() {
        return (sInstance==null)? (sInstance = new CkyEther()) : sInstance;
    }

    private CkyEther() {
        mGasPrice = 500000L;
        mGasLimit = 250000L;
    }

    /**
     * 초기 환경설정 정보
     * @param gethUrl
     * @param passcode
     * @param mainAddr
     * @param contractAddr
     * @return
     */
    public boolean setup(String gethUrl, String passcode, String mainAddr, String contractAddr ) {

        if( TextUtils.isEmpty(gethUrl) || TextUtils.isEmpty(gethUrl) || TextUtils.isEmpty(gethUrl) || TextUtils.isEmpty(gethUrl) ) {
            return false;
        }
        /**
         * 추가적인 변수 정합성 체크 필요
         */

        mPrivateGethUrl = gethUrl;

        mPassCode = passcode;

        mMainAddr = mainAddr;

        mContractAddr = contractAddr;

        mIsSetupCompleted = true;
        return true;
    }

    /**
     * 앱인증정보 로드
     * @param context
     * @return
     */
    public boolean loadAppHashValues(Context context) {
        mAppHashValue = CkyEther.getPackageHashKey(context);
        mCertSHA1Fingerprint = CkyEther.getCertificateSHA1Fingerprint(context);
        mCertSigHashValue = CkyEther.getCertificationHash(context);

        CkyLog.r("[블록쉴드]앱해시값: " + mAppHashValue);
        CkyLog.r("[블록쉴드]인증서 지문: " + mCertSHA1Fingerprint);
        CkyLog.r("[블록쉴드]인증서 해시값: " + mCertSigHashValue);

        /*
        체크 r1 : kIuQVh2/MS1wzBDlDqchgZT6SXs=
        체크 r2 : A1:86:FE:6E:1C:3B:A3:2E:74:B7:D8:DD:8B:27:24:E0:58:A3:BF:1B
        체크 r3 : oYb+bhw7oy50t9jdiyck4Fijvxs=
         */
        return !TextUtils.isEmpty(mAppHashValue) &&
               !TextUtils.isEmpty(mCertSHA1Fingerprint) &&
               !TextUtils.isEmpty(mCertSigHashValue);
    }

    public static String getPrivateGethUrl() {
        return mPrivateGethUrl;
    }

    public String getMainAddr() {
        return mMainAddr;
    }

    public String getContractAddr() {
        return mContractAddr;
    }

    public String getAppUniqId() {
        return mAppUniqId;
    }

    public void setAppUniqId(String appUniqId) {
        mAppUniqId = appUniqId;
    }

    public String getAppHashValue() {
        return mAppHashValue;
    }

    public String getCertSHA1Fingerprint() {
        return mCertSHA1Fingerprint;
    }

    public String getCertSigHashValue() {
        return mCertSigHashValue;
    }

    /**
     * web3j 초기화
     * @return
     */
    public String prepareTransaction() {
        if( !mIsSetupCompleted || !mIsPrepared )
            return "personalUnlockAccount 실패: not initilize";

        try {
            CkyLog.r("[블록쉴드]mPrivateGethUrl: " + mPrivateGethUrl);

            HttpService url = new HttpService(mPrivateGethUrl);
            mWeb3j = Web3jFactory.build(url);
            Admin admin = AdminFactory.build(url);

            EthAccounts ethAccounts = mWeb3j.ethAccounts().sendAsync().get();
            String accounts[] = ethAccounts.getAccounts().toArray(new String[0]);
            CkyLog.r("[이더리움] getAccounts : "+accounts);

            if( accounts != null && accounts.length > 0 ) {
                CkyLog.r( "[이더리움] accounts.length : " + accounts.length);
                for (int i = 0; i < accounts.length; i++)
                    CkyLog.r( "[이더리움] account[" + i + "] : " + accounts[i]);
            }

            PersonalUnlockAccount personalUnlockAccount =
                admin.personalUnlockAccount(accounts[0], mPassCode).sendAsync().get();

            if( personalUnlockAccount.hasError() ) {
                Response.Error error = personalUnlockAccount.getError();
                CkyLog.r("[이더리움] 어드민락 해제 error : " + error.getData());
                CkyLog.r("[이더리움] 어드민락 해제 error : " + error.getMessage());
                return "personalUnlockAccount 실패: "+error.getMessage();
            }
            CkyLog.r("[이더리움] 어드민락 해제여부 : " + personalUnlockAccount);
        } catch ( Exception e ) {
            CkyLog.e("[이더리움] prepareTransaction() 오류 : " + e.toString());
            return "personalUnlockAccount 실패 e: "+e;
        }
        return null;
    }

    public int getRegistCountTransaction() {
        if( !mIsSetupCompleted || !mIsPrepared )
            return -1;

        try {
            Function f = new Function("getRegistedCount",
                    Arrays.<Type>asList(),
                    Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
            String fe = FunctionEncoder.encode(f);

            EthCall response = mWeb3j.ethCall(
                    Transaction.createEthCallTransaction(mMainAddr, mContractAddr, fe),
                    DefaultBlockParameterName.LATEST).sendAsync().get();

            if( response.hasError() == true ) {
                Response.Error error = response.getError();
                CkyLog.r("[이더리움] getRegistCountTransaction error : " + error.getData());
                CkyLog.r("[이더리움] getRegistCountTransaction error : " + error.getMessage());
                return -1;
            }
            CkyLog.r("[이더리움] getRegistCountTransaction : getValue(): " + response.getValue());

            List<Type> resultTypes = FunctionReturnDecoder.decode(response.getValue(), f.getOutputParameters());
            if( resultTypes != null && resultTypes.isEmpty() == false ) {
                Type resultType = resultTypes.get(0);
                CkyLog.r("[이더리움] getRegistCountTransaction : resultType: "+resultType.getTypeAsString());
                CkyLog.r("[이더리움] getRegistCountTransaction : resultValue: "+resultType.getValue());
                return CkyUtil.parseInt(resultType.getValue().toString(), -1);
            }
        } catch (Exception e) {
            CkyLog.e("[이더리움] getRegistCountTransaction() 오류 : " + e.toString());
        }
        return -1;
    }

    /**
     * 앱인증정보 조회
     * @param uniqId
     * @param hashValue
     * @return
     */
    public int checkTransaction(String uniqId, String hashValue) {

        if( !mIsSetupCompleted || !mIsPrepared )
            return -1;
        else if( TextUtils.isEmpty(uniqId) || TextUtils.isEmpty(hashValue) )
            return -1;

        CkyLog.r("[이더리움] checkTransaction uniqId : " + uniqId);
        CkyLog.r("[이더리움] checkTransaction hashValue : " + hashValue);

        try {
            //List<Type> inputParams = Arrays.<Type>asList();
            List<Type> inputParams = new ArrayList<>();
            inputParams.add(new Utf8String(uniqId));
            inputParams.add(new Utf8String(hashValue));
            List<TypeReference<?>> outputParams = Arrays.<TypeReference<?>>asList(new TypeReference<Int8>() {});

            Function f = new Function("check",inputParams,outputParams);
            String fe = FunctionEncoder.encode(f);

            EthCall response = mWeb3j.ethCall(
                    Transaction.createEthCallTransaction(mMainAddr, mContractAddr, fe),
                    DefaultBlockParameterName.LATEST).sendAsync().get();

            if( response.hasError() == true ) {
                Response.Error error = response.getError();
                CkyLog.r("[이더리움] checkTransaction error.getData() : " + error.getData());
                CkyLog.r("[이더리움] checkTransaction error.getMessage() : " + error.getMessage());
                return -1;
            }
            CkyLog.r("[이더리움] checkTransaction : getValue(): " + response.getValue());

            List<Type> resultTypes = FunctionReturnDecoder.decode(response.getValue(), f.getOutputParameters());
            if( resultTypes != null && resultTypes.isEmpty() == false ) {
                Type resultType = resultTypes.get(0);
                CkyLog.r("[이더리움] checkTransaction : resultType: "+resultType.getTypeAsString());
                CkyLog.r("[이더리움] checkTransaction : resultValue: "+resultType.getValue());
                return CkyUtil.parseInt(resultType.getValue().toString(), -1);
            } else {
                CkyLog.e("[이더리움] checkTransaction : resultTypes: "+resultTypes);
            }
        } catch (Exception e) {
            CkyLog.e("[이더리움] checkTransaction() 오류 : " + e.toString());
        }
        return -1;
    }

    public int registAppHashTransaction(String uniqId, String hashValue) {
        if( !mIsSetupCompleted || !mIsPrepared )
            return -1;

        CkyLog.r("[이더리움] registAppHashTransaction uniqId : " + uniqId);
        CkyLog.r("[이더리움] registAppHashTransaction hashValue : " + hashValue);

        try {
            //입력
            List<Type> inputParams = new ArrayList<>();
            Type appUniqId = new Utf8String(uniqId);
            Type appHashValue = new Utf8String(hashValue);
            inputParams.add(appUniqId);
            inputParams.add(appHashValue);

            //출력
            //List<TypeReference<?>> outputParams = new ArrayList<TypeReference<?>>();
            List<TypeReference<?>> outputParams = Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {});

            Function function = new Function("regist", inputParams, outputParams);
            String functionEncoder = FunctionEncoder.encode(function);

            EthGetTransactionCount ethGetTransactionCount = mWeb3j.ethGetTransactionCount(
                    mMainAddr, DefaultBlockParameterName.LATEST).sendAsync().get();

            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            BigInteger gasPrice = BigInteger.valueOf(mGasPrice);
            BigInteger gasLimit = BigInteger.valueOf(mGasLimit);

            Transaction transaction = Transaction.createFunctionCallTransaction(
                    mMainAddr,
                    nonce,
                    gasPrice,
                    gasLimit,
                    mContractAddr,
                    functionEncoder
            );
            CkyLog.r("[이더리움] registAppHashTransaction : 트랜잭션생성");

            EthSendTransaction transactionResponse = mWeb3j.ethSendTransaction(transaction).sendAsync().get();
            String transactionHash = transactionResponse.getTransactionHash();

            if( transactionResponse.hasError() ) {
                Response.Error error = transactionResponse.getError();
                CkyLog.r("[이더리움] registAppHashTransaction error : " + error.getData());
                CkyLog.r("[이더리움] registAppHashTransaction error : " + error.getMessage());
                return 0;
            }
            CkyLog.r("[이더리움] registAppHashTransaction : transactionHash : " + transactionHash);
            CkyLog.r("[이더리움] registAppHashTransaction : outputParams : " + outputParams);

            TransactionReceipt receipt = null;
            int tryCount = 1;
            while( receipt == null ) {
                receipt = getReceipt(transactionHash);
                CkyLog.r("[이더리움] registAppHashTransaction : tryCount : " + tryCount);
                CkyLog.r("[이더리움] registAppHashTransaction : receipt : " + receipt);
                Thread.sleep(1000);
                if( tryCount++ >= 41 )
                    break;
            }

            CkyLog.r("[이더리움] registAppHashTransaction : getResult : " + transactionResponse.getResult());
            CkyLog.r("[이더리움] registAppHashTransaction : getOutputParameters : " + function.getOutputParameters());

            CkyLog.r("[이더리움] registAppHashTransaction : getBlockNumber : " + receipt.getBlockNumber());
            CkyLog.r("[이더리움] registAppHashTransaction : getBlockHash : " + receipt.getBlockHash());
            CkyLog.r("[이더리움] registAppHashTransaction : getGasUsed : " + receipt.getGasUsed());
            if( outputParams != null && outputParams.isEmpty() == false ) {
                TypeReference<?> resultType = outputParams.get(0);

                TypeReference<?> r = function.getOutputParameters().get(0);
                CkyLog.r("[이더리움] registAppHashTransaction : outputParams.size(): "+outputParams.size());
                CkyLog.r("[이더리움] registAppHashTransaction : resultValue: "+resultType);
                CkyLog.r("[이더리움] registAppHashTransaction : resultType.equals(Boolean.TRUE): "+resultType.equals(Boolean.TRUE));
                CkyLog.r("[이더리움] registAppHashTransaction : resultType.equals(Boolean.FALSE): "+resultType.equals(Boolean.FALSE));
                CkyLog.r("[이더리움] registAppHashTransaction : resultType.getClassType(): "+resultType.getClassType());

                org.web3j.abi.datatypes.Bool btrue = new org.web3j.abi.datatypes.Bool(true);
                org.web3j.abi.datatypes.Bool bfalse = new org.web3j.abi.datatypes.Bool(false);

                CkyLog.r("[이더리움] registAppHashTransaction : resultType.equals(btrue): "+resultType.equals(btrue));
                CkyLog.r("[이더리움] registAppHashTransaction : resultType.equals(bfalse): "+resultType.equals(bfalse));
            }
        } catch (Exception e) {
            CkyLog.e("registAppHashTransaction 오류 e: "+ e);
            return -1;
        }
        return 1;
    }

    public TransactionReceipt getReceipt(String transactionHash)
            throws Exception
    {
        EthGetTransactionReceipt receipt = mWeb3j
                .ethGetTransactionReceipt(transactionHash)
                .sendAsync()
                .get();

        return receipt.getTransactionReceipt();
    }
    /*
    TransactionReceipt receipt = null;
            int tryCount = 1;
            while( receipt == null ) {
                receipt = getReceipt(web3j, transactionHash);
                CkyLog.r("tryCount : " + tryCount);
                CkyLog.r("receipt : " + receipt);

                Thread.sleep(1000);
                if( tryCount++ >= 40 )
                    break;
            }
            CkyLog.r("getResult() : " + transactionResponse.getResult());
            CkyLog.r("functionResults : " + functionResults);
            CkyLog.r("functionResults : " + function.getOutputParameters());
     */

    public static String getPackageHashKey(Context ctx) {
        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            CkyLog.r("[이더리움]getPackageHashKey applicationInfo : "+info.applicationInfo);
            CkyLog.r("[이더리움]getPackageHashKey applicationInfo.sourceDir : "+info.applicationInfo.sourceDir);

            byte[] data = CkyFileMgr.readFile(info.applicationInfo.sourceDir);
            CkyLog.r("[이더리움]getPackageHashKey data.length : "+((data!=null)?data.length:0));
            if( data != null ) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(data);
                return Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (Exception ex) {
            CkyLog.e("getPackageHashKey 오류 e: "+ e);
        }
        return null;
    }

    public static String getCertificationHash(Context ctx) {
        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return  Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (Exception ex) {
            CkyLog.e("getCertificationHash 오류 e: "+ e);
        }
        return null;
    }

    public static String getCertificateSHA1Fingerprint(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        String packageName = ctx.getPackageName();
        int flags = PackageManager.GET_SIGNATURES;

        PackageInfo packageInfo = null;
        X509Certificate c = null;

        try {
            packageInfo = pm.getPackageInfo(packageName, flags);

            Signature[] signatures = packageInfo.signatures;
            byte[] cert = signatures[0].toByteArray();
            InputStream input = new ByteArrayInputStream(cert);
            CertificateFactory cf = null;

        } catch (PackageManager.NameNotFoundException e) {
            CkyLog.e("getCertificateSHA1Fingerprint 오류 e: "+ e);
            return null;
        }

        try {
            cf = CertificateFactory.getInstance("X509");
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (Exception ex) {
            CkyLog.e("getCertificateSHA1Fingerprint 오류 e: "+ e);
            return null;
        }
        String hexString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getEncoded());
            hexString = byte2HexFormatted(publicKey);
        } catch (Exception ex) {
            CkyLog.e("getCertificateSHA1Fingerprint 오류 e: "+ e);
        }
        return hexString;
    }

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }

    public String getBalance(String address)
    {
        String result = null;
        EthGetBalance ethGetBalance = null;
        try {

            //이더리움 노드에게 지정한 Address 의 잔액을 조회한다.
            ethGetBalance = mWeb3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger wei = ethGetBalance.getBalance();

            //wei 단위를 ETH 단위로 변환 한다.
            result = Convert.fromWei(wei.toString() , Convert.Unit.ETHER).toString();
        } catch (Exception e) {
            CkyLog.e("getBalance 오류 e: "+ e);
        }
        return result;
    }
}

/*

        String r1 = CkyEther.getPackageHashKey(this);
        String r2 = CkyEther.getCertificateSHA1Fingerprint(this);
        String r3 = CkyEther.getCertificationHash(this);
        //CkyEther.getBalance("071e94091e58708dce78c24ceef08cc1ec9d11f4");
        CkyLog.r("체크 r1 : "+r1);
        CkyLog.r("체크 r2 : "+r2);
        CkyLog.r("체크 r3 : "+r3);

        //new DownloadFilesTask().execute();

        Web3j web3 = Web3jFactory.build(new HttpService("http://192.168.0.12:8123"));
        String result = null;
        EthGetBalance ethGetBalance = null;
        try {
            EthAccounts ethAccounts = web3.ethAccounts().sendAsync().get();
            String accounts[] = ethAccounts.getAccounts().toArray(new String[0]);
            CkyLog.r("체크 accounts : "+accounts);
            if( accounts != null && accounts.length > 0 ) {
                for( String account : accounts )
                    CkyLog.r("체크 account : "+account);
            }

            //wei 단위를 ETH 단위로 변환 한다.
            //result = Convert.fromWei(wei.toString() , Convert.Unit.ETHER).toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
            CkyLog.r("e1 : "+e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            CkyLog.r("e2 : "+e);
        } catch (Exception e) {
            CkyLog.r("e3 : "+e);
        }
 */