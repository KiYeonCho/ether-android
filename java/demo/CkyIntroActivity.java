package kr.ac.koreauniv.blockchain.demo.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import kr.ac.koreauniv.R;
import kr.ac.koreauniv.util.CkyEther;
import kr.ac.koreauniv.util.CkyFileMgr;
import kr.ac.koreauniv.util.CkyLog;
import kr.ac.koreauniv.util.CkyUtil;
import java.io.File;

/**
 * Created by KiYeon Cho / Shinhan DS
 * web3j관련 처리만 참조하세요
 */

public final class CkyIntroActivity extends CkyBaseIntroActivity
{
    public CkyIntroActivity()
    {
        super(R.layout.mbi_intro_activity);
        //생략.....
        //생략.....
        //생략.....                
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //생략.....
        //생략.....
        //생략.....        
    }

    private CkyEther mEther;
    private int mEtherAuthResult;
    private ProgressDialog mProgressDialog;

    protected void onInitCompleted()
    {
        File downloadFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String downloadFilePath = downloadFile.getAbsolutePath()+File.separator +"ipaddr.txt";
        byte[] data = CkyFileMgr.readFile(downloadFilePath);
        if( data != null && data.length > 0 ) {
            CkyEther.PRIVATE_GETH_URL = "http://"+new String(data)+":8123";
        }
        CkyLog.r("[이더리움]Geth URL: " + CkyEther.PRIVATE_GETH_URL);


        mEther = CkyEther.sharedInstance();
        mEther.loadAppHashValues(mApp);
        String message =
                "1-1) 앱해시\n"+mEther.getAppHashValue()+"\n"+
                "1-2) 인증서\n"+mEther.getCertSHA1Fingerprint()+"\n\n"+
                "1-3) 서명\n"+mEther.getCertSigHashValue();

        alert.showAlertMsg(
                "[블록실드]\n1)앱설치정보 추출",
                message,
                "확인", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        mProgressDialog = new ProgressDialog(CkyIntroActivity.this);
                        mProgressDialog.setMessage("[블록실드] 2)이더리움 연결\n\n초기화");
                        mProgressDialog.setIndeterminate(true);
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.show();

                        mHandler.sendEmptyMessage(HM_ETHER0);
                        //mHandler.sendEmptyMessageDelayed(HM_ETHER0, 1500);
                    }
                },
                null, null
                , false);

        /*Intent intent = new Intent(this, SwfMbiMainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();

        mMbiApp.clearLoginInfoWithCookie();
        SwfUtil.logWithMint(SwfBaseIntroActivity.class, "[SWF] onInitCompleted()", "start(SwfMbiMainActivity)");*/
    }


    private final int HM_ETHER0 = 1003;
    private final int HM_ETHER1 = 1004;
    private final int HM_ETHER2 = 1005;
    private final int HM_ETHER3 = 1006;

    protected void onHandle(int what) {
        switch(what) {
            case HM_ETHER0:{
                String error = CkyEther.sharedInstance().prepareTransaction();
                if( error == null ) {
                    mHandler.sendEmptyMessageDelayed(HM_ETHER1, 2000);
                } else {
                    mProgressDialog.dismiss();
                    alert.showAlertMsg(error, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mProgressDialog = new ProgressDialog(CkyIntroActivity.this);
                            mProgressDialog.setMessage("[블록실드]\n2)이더리움 연결\n\n초기화");
                            mProgressDialog.setIndeterminate(true);
                            mProgressDialog.setCancelable(false);
                            mProgressDialog.show();

                            mHandler.sendEmptyMessageDelayed(HM_ETHER0, 1500);
                        }
                    }, false);
                }
            }
            break;
            case HM_ETHER1:{
                mProgressDialog.setTitle("[블록실드] 3)앱인증 조회");
                mProgressDialog.setMessage("처리 중..");
                CkyLog.r("[이더리움]앱인증 조회");

                String[] versionAndCode = CkyUtil.getVersionAndCode(CkyIntroActivity.this);
                mEther.setAppUniqId(mApp.getPackageName()+"_"+versionAndCode[1]);

                mEtherAuthResult = mEther.checkTransaction(mEther.getAppUniqId(), mEther.getAppHashValue());
                //mEtherAuthResult = mEther.checkTransaction("1111", "2222");
                int count = mEther.getRegistCountTransaction();
                CkyLog.r("[이더리움]앱인증 등록 정보 수 : " +count );
                mHandler.sendEmptyMessageDelayed(HM_ETHER2, 1500);
            }
            break;

            case HM_ETHER2: {
                if( mEtherAuthResult == 0 ) {
                    mProgressDialog.setTitle("[블록실드] 4)앱인증 완료");
                    mProgressDialog.setMessage("잠시만 기다려주세요.");
                    CkyLog.r("[이더리움]: 앱인증 완료" );
                    mHandler.sendEmptyMessageDelayed(HM_ETHER3, 1500);
                } else {
                    mProgressDialog.dismiss();

                    String resultAuthName = "인증성공";
                    if (mEtherAuthResult == -2) resultAuthName = "미등록";
                    else if (mEtherAuthResult == -3) resultAuthName = "불일치";

                    resultAuthName = resultAuthName + "(" + mEtherAuthResult + ")";
                    String message = "인증조회 결과 : " + resultAuthName + "\n\n" +
                            "▫인증성공 :0\n▫미등록 :-2\n▫인증불일치:-3";

                    alert.showAlertMsg(
                            "[블록실드] 인증조회 결과",
                            message,
                            "등록", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mEther.prepareTransaction();

                                    if (-1 != mEther.registAppHashTransaction(mEther.getAppUniqId(), mEther.getAppHashValue())) {

                                        Toast.makeText(CkyIntroActivity.this, "앱해시 등록 성공", Toast.LENGTH_SHORT).show();
                                        mHandler.sendEmptyMessageDelayed(HM_ETHER1, 1500);

                                    } else {
                                        Toast.makeText(CkyIntroActivity.this, "앱해시 등록 실패", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            "종료", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }
                            , false);
                }
            }
            break;

            case HM_ETHER3:{
                if( mProgressDialog != null )
                    mProgressDialog.dismiss();
                startMainActivity();
            }
            break;

        }
    }


    /**
     * 앱의 메인화면(Activity)로 이동함
     */
    protected void startMainActivity() {

        Intent intent = new Intent(this, CkyMainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }


}
