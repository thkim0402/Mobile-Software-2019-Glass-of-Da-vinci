package com.e.glassofdavinci

import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    //request 값으로 받아올 변수를 설정(바꿀 필요없으므로 val)
    private val PERMISSIONS_REQUEST_CODE = 100

    //필요한 리퀘스트를 담아놓은 어레이(바꿀 필요없으므로 val)
    private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    //나는 후면 카메라를 사용할 것이다 <-> 전면 쓰고 싶으면 BACK을 FRONT로(혹시나 바꿀 수 있으니 var로 처리)
    private var CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_FRONT

    private var myCameraPreview: MyCameraPreview? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //상태바를 안보이도록 한다.
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        //화면이 켜진 상태른 유지한다.
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_main)

        //------------------------권한채크 파트----------------------------//

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            //OS가 마쉬멜로 이상일 경우 권한체크
            val permissionCheckCamera
                = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            val permissionCheckStorage
                = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

            //만약 두개다 권한이 있을경우
            if (permissionCheckCamera == PackageManager.PERMISSION_GRANTED
                && permissionCheckStorage == PackageManager.PERMISSION_GRANTED){

                Log.d("Debug","권한 이미있음")
                //startCamera()
            }else{ //권한이 없으면
                Log.d("Debug","권한 없음")

                ActivityCompat.requestPermissions(this,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE)
            }
        }else{
            //OS가 마쉬멜로 이전일 경우 권한체크가 필요없으므로
            Log.d("Debug","마쉬멜로 버전 이하인 관계로 권한이 이미 있음")
            //startCamera()
        }

        //------------------------권한채크 파트 끝--------------------------//
    }

    //onCreate 의 ActivityCompat.reQuestPermission 로 권한체크 하면서 리턴데이터가 이 함수로 전송됨 : 권한이 거부됐는지, 잘 됬는지 알려줌 //
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // grantResults[0] 거부 -> -1
        // grantResults[0] 허용 -> 0 (PackageManager.PERMISSION_GRANTED)

        Log.d("Debug", "requestCode : $requestCode, grantResults size : ${grantResults.size}")

        if(requestCode == PERMISSIONS_REQUEST_CODE){
            //권한이

            var check_result = true

            for(result in grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    check_result = false
                    break
                }
            }

            if(check_result){
                //startCamera()
            }else{
                Log.d("Debug","권한 거부")
            }
        }
    }
}
