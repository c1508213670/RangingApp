package com.dueeeke.dkplayer.activity.api;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.SoundPoolManager;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.activity.MainActivity;
import com.dueeeke.dkplayer.util.IntentKeys;
import com.dueeeke.dkplayer.widget.component.DebugInfoView;
import com.dueeeke.dkplayer.widget.component.PlayerMonitor;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videocontroller.component.CompleteView;
import com.dueeeke.videocontroller.component.ErrorView;
import com.dueeeke.videocontroller.component.GestureView;
import com.dueeeke.videocontroller.component.PrepareView;
import com.dueeeke.videocontroller.component.TitleView;
import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.player.DashBoard;
import com.dueeeke.videoplayer.util.L;
import com.orhanobut.logger.Logger;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;



import android.content.Intent;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.io.IOException;
/**
 * 播放器演示
 */

public class PlayerActivity extends BaseActivity<VideoView> implements View.OnClickListener{

    // private static final String THUMB = "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg"; // 封面图

    private final static String SEND_IP = "10.10.10.134";     // 树莓派IP
    private final static int SEND_PORT = 45452;               // 树莓派端口号
    private final static int RECEIVE_PORT = 45454;            // 安卓机接收端口号
   // private static final String LIVE_URL = "rtsp://" + SEND_IP + ":8554/unicast";   // rtsp链接
     private static final String LIVE_URL = "rtsp://192.168.56.1:8553/ssr";   // 测试链接


    private boolean listenStatus = true;  //接收线程的循环标识
    private byte[] receiveInfo;     //接收报文信息
    private byte[] buf;

    private DatagramSocket receiveSocket;
    private DatagramSocket sendSocket;
    private InetAddress serverAddr;
    private SendHandler sendHandler = new SendHandler();
    private ReceiveHandler receiveHandler = new ReceiveHandler();

    private Button btnSendUDP;
    private VideoView videoView;

    private Handler handler = new Handler();
    private Runnable runnable;
    private int TIME = 500;  // 单位毫秒  即每隔0.5s请求一次.
    private int TIME_FLAG = 0;  // 用来记录奇偶次请求

    private float warningDistance = 0;
    private String currentDistance = "0";
    private String angle_vertical = "0";
    private String currentangle="1";
    private String angle_horizontal = "0";
    private boolean powerStatus = true;
    private boolean warning = false;
    private boolean up = false;
    private boolean down = false;
    private boolean left = false;
    private boolean right = false;
    private boolean center = false;

    private Button setWarningDistanceView;
    private Button setangleview;
    private Button warningDistanceView;
    private Button currentDistanceView;
    private Button currentangleview;
    private Button powerStatusView;
    private ImageView warningViewLight;
    private ImageView warningViewDark;
    private ImageView upViewLight;
    private ImageView upViewDark;
    private ImageView downViewLight;
    private ImageView downViewDark;
    private ImageView leftViewLight;
    private ImageView leftViewDark;
    private ImageView rightViewLight;
    private ImageView rightViewDark;
    private ImageView centerViewLight;
    private ImageView centerViewDark;
    private DashBoard Dashboardview;
    /*

     */
    private String edtTxt_Addr="10.10.10.134";
    private int edtTxt_Port=1234;
    private Button btn_Send;
    private Button no_Send;
    private Button con;
    private Button cut;
    private MediaPlayer mediaPlayer01;


    private TcpClientConnector connector;


    /***
     * 控件初始化
     */

//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        initViews();
//        sub_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new HttpThread().start();
//            }
//        });
//
//    }

    private static double getDoubleValue(String str)
    {
        double d = 0;

        if(str!=null && str.length()!=0)
        {
            StringBuffer bf = new StringBuffer();

            char[] chars = str.toCharArray();
            for(int i=0;i<chars.length;i++)
            {
                char c = chars[i];
                if(c>='0' && c<='9')
                {
                    bf.append(c);
                }
                else if(c=='.')
                {
                    if(bf.length()==0)
                    {
                        continue;
                    }
                    else if(bf.indexOf(".")!=-1)
                    {
                        break;
                    }
                    else
                    {
                        bf.append(c);
                    }
                }
                else
                {
                    if(bf.length()!=0)
                    {
                        break;
                    }
                }
            }
            try
            {
                d = Double.parseDouble(bf.toString());
            }
            catch(Exception e)
            {}
        }

        return d;
    }




    class ButtonClickEvent2 implements View.OnClickListener{
        public void onClick(View v) {
            if (v == con) {
                Logger.d(".......");
                connector = TcpClientConnector.getInstance();
                final EditText commandInput1 = new EditText(PlayerActivity.this);
                final EditText commandInput2 = new EditText(PlayerActivity.this);
                new AlertDialog.Builder(PlayerActivity.this)
                        .setTitle("请输入链接的服务器ip:")
                        .setView(commandInput1)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    edtTxt_Addr = commandInput1.getText().toString();
                                    edtTxt_Port = 1234;
                                    System.out.println(edtTxt_Addr+edtTxt_Port);
                                    connector.createConnect(edtTxt_Addr,edtTxt_Port);   //调试使用

                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "设置失败，当前距离IP：" + edtTxt_Addr , Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(), "取消设置当前距离", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();

                // connector.createConnect(edtTxt_Addr,edtTxt_Port);

            }

        }
        }

    class ButtonClickEvent3 implements View.OnClickListener{
        public void onClick(View v) {
            if (v==cut) {

                try{   //断开与服务器的连接
                    connector.disconnect();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }}


    class ButtonClickEvent1 implements View.OnClickListener{
        public void onClick(View v){
            if (v == no_Send){
                //发送数据


                new TCPcutThread().start();

            }
        }
    }
    class ButtonClickEvent implements View.OnClickListener{
        public void onClick(View v){
            if (v == btn_Send){
                //发送数据
                new TCPSendThread().start();
            }

        }
    }


    /*

     */

    public static void start(Context context, String url, String title, boolean isLive) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(IntentKeys.URL, url);
        intent.putExtra(IntentKeys.IS_LIVE, isLive);
        intent.putExtra(IntentKeys.TITLE, title);
        context.startActivity(intent);
    }


    private void updateUILight(){
        if(warning){
            mediaPlayer01.setLooping(true);
            mediaPlayer01.start();
        }else{
            mediaPlayer01.pause();
        }
        if(Integer.parseInt(currentangle)%7==0 || Integer.parseInt(currentangle)%30==0){
            currentDistanceView.setText(currentDistance + "米");
        }
        if(powerStatus){
            powerStatusView.setText("有");
        }else{
            powerStatusView.setText("无");
        }

        // 这里有障碍物就闪一下红色
        if(warning){
            warningViewLight.setImageResource(R.drawable.red_light);
            warningViewDark.setImageResource(R.drawable.red);
            warningViewLight.setVisibility(View.VISIBLE);
            warningViewDark.setVisibility(View.INVISIBLE);
        }

//        if(warning){
//            warningViewLight.setImageResource(R.drawable.red_light);
//            warningViewDark.setImageResource(R.drawable.red);
//            warningViewLight.setVisibility(View.VISIBLE);
//            warningViewDark.setVisibility(View.INVISIBLE);
//        }else{
//            warningViewLight.setImageResource(R.drawable.green_light);
//            warningViewDark.setImageResource(R.drawable.green);
//            warningViewLight.setVisibility(View.VISIBLE);
//            warningViewDark.setVisibility(View.INVISIBLE);
//        }
//
//        if(up){
//            upViewLight.setImageResource(R.drawable.red_obstacles_light);
//            upViewDark.setImageResource(R.drawable.red_obstacles);
//            upViewLight.setVisibility(View.VISIBLE);
//            upViewDark.setVisibility(View.INVISIBLE);
//
//        }else{
//            upViewLight.setImageResource(R.drawable.green_obstacles_light);
//            upViewDark.setImageResource(R.drawable.green_obstacles);
//            upViewLight.setVisibility(View.VISIBLE);
//            upViewDark.setVisibility(View.INVISIBLE);
//        }
//        if(down){
//            downViewLight.setImageResource(R.drawable.red_obstacles_light);
//            downViewDark.setImageResource(R.drawable.red_obstacles);
//            downViewLight.setVisibility(View.VISIBLE);
//            downViewDark.setVisibility(View.INVISIBLE);
//        }else{
//            downViewLight.setImageResource(R.drawable.green_obstacles_light);
//            downViewDark.setImageResource(R.drawable.green_obstacles);
//            downViewLight.setVisibility(View.VISIBLE);
//            downViewDark.setVisibility(View.INVISIBLE);
//        }
//        if(left){
//            leftViewLight.setImageResource(R.drawable.red_obstacles_light);
//            leftViewDark.setImageResource(R.drawable.red_obstacles);
//            leftViewLight.setVisibility(View.VISIBLE);
//            leftViewDark.setVisibility(View.INVISIBLE);
//        }else{
//            leftViewLight.setImageResource(R.drawable.green_obstacles_light);
//            leftViewDark.setImageResource(R.drawable.green_obstacles);
//            leftViewLight.setVisibility(View.VISIBLE);
//            leftViewDark.setVisibility(View.INVISIBLE);
//        }
//        if(right){
//            rightViewLight.setImageResource(R.drawable.red_obstacles_light);
//            rightViewDark.setImageResource(R.drawable.red_obstacles);
//            rightViewLight.setVisibility(View.VISIBLE);
//            rightViewDark.setVisibility(View.INVISIBLE);
//        }else{
//            rightViewLight.setImageResource(R.drawable.green_obstacles_light);
//            rightViewDark.setImageResource(R.drawable.green_obstacles);
//            rightViewLight.setVisibility(View.VISIBLE);
//            rightViewDark.setVisibility(View.INVISIBLE);
//        }
//        if(center){
//            centerViewLight.setImageResource(R.drawable.red_obstacles_light);
//            centerViewDark.setImageResource(R.drawable.red_obstacles);
//            centerViewLight.setVisibility(View.VISIBLE);
//            centerViewDark.setVisibility(View.INVISIBLE);
//        }else{
//            centerViewLight.setImageResource(R.drawable.green_obstacles_light);
//            centerViewDark.setImageResource(R.drawable.green_obstacles);
//            centerViewLight.setVisibility(View.VISIBLE);
//            centerViewDark.setVisibility(View.INVISIBLE);
//        }
    }

    private void updateUIDark(){
        if(warning){
            mediaPlayer01.setLooping(true);
            mediaPlayer01.start();
        }else{
            mediaPlayer01.pause();
        }
        if(Integer.parseInt(currentangle)%7==0 || Integer.parseInt(currentangle)%30==0){
            currentDistanceView.setText(currentDistance + "米");
        }
        if(powerStatus){
            powerStatusView.setText("有");
        }else{
            powerStatusView.setText("无");
        }
        // 这里不管有没有障碍物都是绿色
        warningViewLight.setImageResource(R.drawable.green_light);
        warningViewDark.setImageResource(R.drawable.green);
        warningViewLight.setVisibility(View.VISIBLE);
        warningViewDark.setVisibility(View.INVISIBLE);
//        if(warning){
//            warningViewLight.setImageResource(R.drawable.red_light);
//            warningViewDark.setImageResource(R.drawable.red);
//            warningViewLight.setVisibility(View.INVISIBLE);
//            warningViewDark.setVisibility(View.VISIBLE);
//        }else{
//            warningViewLight.setImageResource(R.drawable.green_light);
//            warningViewDark.setImageResource(R.drawable.green);
//            warningViewLight.setVisibility(View.INVISIBLE);
//            warningViewDark.setVisibility(View.VISIBLE);
//        }
//        if(up){
//            upViewLight.setImageResource(R.drawable.red_obstacles_light);
//            upViewDark.setImageResource(R.drawable.red_obstacles);
//            upViewLight.setVisibility(View.INVISIBLE);
//            upViewDark.setVisibility(View.VISIBLE);
//
//        }else{
//            upViewLight.setImageResource(R.drawable.green_obstacles_light);
//            upViewDark.setImageResource(R.drawable.green_obstacles);
//            upViewLight.setVisibility(View.VISIBLE);
//            upViewDark.setVisibility(View.INVISIBLE);
//        }
//        if(down){
//            downViewLight.setImageResource(R.drawable.red_obstacles_light);
//            downViewDark.setImageResource(R.drawable.red_obstacles);
//            downViewLight.setVisibility(View.INVISIBLE);
//            downViewDark.setVisibility(View.VISIBLE);
//        }else{
//            downViewLight.setImageResource(R.drawable.green_obstacles_light);
//            downViewDark.setImageResource(R.drawable.green_obstacles);
//            downViewLight.setVisibility(View.VISIBLE);
//            downViewDark.setVisibility(View.INVISIBLE);
//        }
//        if(left){
//            leftViewLight.setImageResource(R.drawable.red_obstacles_light);
//            leftViewDark.setImageResource(R.drawable.red_obstacles);
//            leftViewLight.setVisibility(View.INVISIBLE);
//            leftViewDark.setVisibility(View.VISIBLE);
//        }else{
//            leftViewLight.setImageResource(R.drawable.green_obstacles_light);
//            leftViewDark.setImageResource(R.drawable.green_obstacles);
//            leftViewLight.setVisibility(View.VISIBLE);
//            leftViewDark.setVisibility(View.INVISIBLE);
//        }
//        if(right){
//            rightViewLight.setImageResource(R.drawable.red_obstacles_light);
//            rightViewDark.setImageResource(R.drawable.red_obstacles);
//            rightViewLight.setVisibility(View.INVISIBLE);
//            rightViewDark.setVisibility(View.VISIBLE);
//        }else{
//            rightViewLight.setImageResource(R.drawable.green_obstacles_light);
//            rightViewDark.setImageResource(R.drawable.green_obstacles);
//            rightViewLight.setVisibility(View.VISIBLE);
//            rightViewDark.setVisibility(View.INVISIBLE);
//        }
//        if(center){
//            centerViewLight.setImageResource(R.drawable.red_obstacles_light);
//            centerViewDark.setImageResource(R.drawable.red_obstacles);
//            centerViewLight.setVisibility(View.INVISIBLE);
//            centerViewDark.setVisibility(View.VISIBLE);
//        }else{
//            centerViewLight.setImageResource(R.drawable.green_obstacles_light);
//            centerViewDark.setImageResource(R.drawable.green_obstacles);
//            centerViewLight.setVisibility(View.VISIBLE);
//            centerViewDark.setVisibility(View.INVISIBLE);
//        }
    }



    class ReceiveHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle b = msg.getData();
            currentDistance = b.getString("distance");
            angle_vertical = b.getString("angle_vertical");
            try{
                float d = Float.parseFloat(currentDistance);
                if(d < warningDistance){
                    warning = true;
                }else{
                    warning = false;
                }

                float t = Float.parseFloat(angle_vertical);
                if(warning){
                    if(t > 135){
                        up = true;
                        down = false;
                    }else if(t < 135){
                        down = true;
                        up = false;
                    }
                }else{
                    up = false;
                    down = false;
                    left = false;
                    right = false;
                    center = false;
                }


            }catch (Exception e){
                Logger.d(e.toString());
            }

//            Logger.d(t);
//            Logger.d(up);


            // 垂直角度 (0, 270)
            // (-45, 135) 对应 (0, 90)
            // (0, 45) -> ...
            // 不太清楚对应规则


            updateUILight();


            // Toast.makeText(PlayerActivity.this, b.getString("distance") + " " + b.getString("angle_vertical") , Toast.LENGTH_SHORT).show();
        }
    }

    class SendHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Toast.makeText(PlayerActivity.this, "成功发送", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected int getLayoutResId() {
        return R.layout.activity_cab;
    }

    @Override
    protected void initView() {
        super.initView();
        mediaPlayer01 = MediaPlayer.create(this, R.raw.warning);
        // 绑定UI控件
        mVideoView = findViewById(R.id.surfaceView);
        Dashboardview = (DashBoard) findViewById(R.id.dash);
        setWarningDistanceView = findViewById(R.id.button);
        setangleview = findViewById(R.id.button11);
        currentangleview = findViewById(R.id.button12);
        setWarningDistanceView.setOnClickListener(this);
        warningDistanceView = findViewById(R.id.button2);
        currentDistanceView = findViewById(R.id.button6);
        powerStatusView = findViewById(R.id.button4);
        warningViewLight = findViewById(R.id.circle);
        warningViewDark = findViewById(R.id.circle_light);
//        upViewLight = findViewById(R.id.above_light);
//        upViewDark = findViewById(R.id.above_dark);
//        downViewLight = findViewById(R.id.below_light);
//        downViewDark = findViewById(R.id.below_dark);
//        leftViewLight = findViewById(R.id.left_light);
//        leftViewDark = findViewById(R.id.left_dark);
//        rightViewLight = findViewById(R.id.right_light);
//        rightViewDark = findViewById(R.id.right_dark);
//        centerViewLight = findViewById(R.id.center_light);
//        centerViewDark = findViewById(R.id.center_dark);
        btn_Send=findViewById(R.id.button7);
        no_Send=findViewById(R.id.button8);
        con=findViewById(R.id.button9);
        cut=findViewById(R.id.button10);

        System.out.println("@@@@oncreate@@@");

        //setContentView(R.layout.activity_main);

        connector = TcpClientConnector.getInstance();   //获取connector实例
        con.setOnClickListener(new ButtonClickEvent2());
        cut.setOnClickListener(new ButtonClickEvent3());
        btn_Send.setOnClickListener(new ButtonClickEvent());
        no_Send.setOnClickListener(new ButtonClickEvent1());

        StandardVideoController controller = new StandardVideoController(this);
        //根据屏幕方向自动进入/退出全屏
        controller.setEnableOrientation(false);

        PrepareView prepareView = new PrepareView(this);//准备播放界面
//            ImageView thumb = prepareView.findViewById(R.id.thumb);//封面图
//            Glide.with(this).load(THUMB).into(thumb);
        controller.addControlComponent(prepareView);

        controller.addControlComponent(new CompleteView(this));//自动完成播放界面

        controller.addControlComponent(new ErrorView(this));//错误界面

        TitleView titleView = new TitleView(this);//标题栏
        controller.addControlComponent(titleView);

//            //根据是否为直播设置不同的底部控制条
//            boolean isLive = true;
//            if (isLive) {
//                controller.addControlComponent(new LiveControlView(this));//直播控制条
//            } else {
//                VodControlView vodControlView = new VodControlView(this);//点播控制条
//                //是否显示底部进度条。默认显示
////                vodControlView.showBottomProgress(false);
//                controller.addControlComponent(vodControlView);
//            }

        GestureView gestureControlView = new GestureView(this);//滑动控制视图
        controller.addControlComponent(gestureControlView);
        //根据是否为直播决定是否需要滑动调节进度
        controller.setCanChangePosition(false);

//            //设置标题
//            String title = intent.getStringExtra("IntentKeys.TITLE");
//            titleView.setTitle(title);

        //注意：以上组件如果你想单独定制，我推荐你把源码复制一份出来，然后改成你想要的样子。
        //改完之后再通过addControlComponent添加上去
        //你也可以通过addControlComponent添加一些你自己的组件，具体实现方式参考现有组件的实现。
        //这个组件不一定是View，请发挥你的想象力😃

        //如果你不需要单独配置各个组件，可以直接调用此方法快速添加以上组件
//            controller.addDefaultControlComponent(title, isLive);

        //竖屏也开启手势操作，默认关闭
//            controller.setEnableInNormal(true);
        //滑动调节亮度，音量，进度，默认开启
//            controller.setGestureEnabled(false);
        //适配刘海屏，默认开启
//            controller.setAdaptCutout(false);

        //在控制器上显示调试信息
        controller.addControlComponent(new DebugInfoView(this));
        //在LogCat显示调试信息
        controller.addControlComponent(new PlayerMonitor());



        //如果你不想要UI，不要设置控制器即可
        mVideoView.setVideoController(controller);

        mVideoView.setUrl(LIVE_URL);

        //保存播放进度
//            mVideoView.setProgressManager(new ProgressManagerImpl());
        //播放状态监听
        mVideoView.addOnStateChangeListener(mOnStateChangeListener);

        //临时切换播放核心，如需全局请通过VideoConfig配置，详见MyApplication
        //使用IjkPlayer解码
//            mVideoView.setPlayerFactory(IjkPlayerFactory.create());
        //使用ExoPlayer解码
//             mVideoView.setPlayerFactory(ExoMediaPlayerFactory.create());
        //使用MediaPlayer解码
//            mVideoView.setPlayerFactory(AndroidMediaPlayerFactory.create());

        mVideoView.start();




//        //播放其他视频
//        EditText etOtherVideo = findViewById(R.id.et_other_video);
//        findViewById(R.id.btn_start_play).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mVideoView.release();
//                mVideoView.setUrl(etOtherVideo.getText().toString());
//                mVideoView.start();
//            }
//        });


        //进入Activity时开启接收报文线程
//        new UdpReceiveThread().start();

//        // 接收雷达信号
//        btnSendUDP = (Button) findViewById(R.id.button);
//        videoView = (VideoView) findViewById(R.id.surfaceView);
//
//        btnSendUDP.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //点击按钮则发送UDP报文
//                new UdpSendThread().start();
//            }
//        });

        runnable = new Runnable( ) {
            public void run ( ) {
                try {
                    // 开始发送线程
                    new HttpThread().start();

                    // 根据一些变量，更新当前UI
                    if(TIME_FLAG == 0){
                        updateUIDark();
                        TIME_FLAG = 1;
                    }else{
                        updateUILight();
                        TIME_FLAG = 0;
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, TIME);
                //postDelayed(this,2000)方法安排一个Runnable对象到主线程队列中
            }
        };
        handler.postDelayed(runnable, TIME); // 开始Timer




    }

    @Override
    protected void destoryView() {
        super.destoryView();
        handler.removeCallbacks(runnable); //停止Timer
        SoundPoolManager.getInstance(this).release();
        listenStatus = false;
        receiveSocket.close();

    }


    /*
     *   UDP数据发送线程
     * */

    public class TCPSendThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                try{
                    System.out.println("#######");
                    System.out.println(1);
                    System.out.println("$$$$$$");

                    connector.send("1");
                    System.out.println("&&&&&&&");
                }catch (IOException e){
                    e.printStackTrace();
                }
                connector.setOnConnectListener(new TcpClientConnector.ConnectListener() {
                    @Override
                    public void onReceiveData(String data) {
                        //Received Data,do somethings.
                        currentangle = data;
                        if (Integer.parseInt(data)%30==0) {
                            currentangle = data;
                            currentangleview.setText(currentangle + "°");
                            Dashboardview.cgangePer(Float.parseFloat(currentangle)/180f);
                            System.out.println("接受到的数据");
                            System.out.println(data);
                        }
                    }
                });
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public class TCPcutThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                try{
                    System.out.println("#######");
                    System.out.println(11);
                    System.out.println("$$$$$$");

                    connector.send("11");
                    System.out.println("&&&&&&&");
                }catch (IOException e){
                    e.printStackTrace();
                }

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public class UdpSendThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
                buf=("[request from android] " + dateFormat.format((date))).getBytes();

                // 创建DatagramSocket对象，使用随机端口
                sendSocket = new DatagramSocket();
                serverAddr = InetAddress.getByName(SEND_IP);

                DatagramPacket outPacket = new DatagramPacket(buf, buf.length,serverAddr, SEND_PORT);
                sendSocket.send(outPacket);
                sendSocket.close();
                new UdpReceiveThread().start();
                sendHandler.sendEmptyMessage(1);

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    //android里面网络请求得在线程里面执行
    class HttpThread extends Thread  {
        @Override
        public void run() {
            super.run();

            try {
                //数据包内容，我这里是用的二进制
                byte[] b = new byte[2];
                b[0] = (byte) 0x69;
                b[1] = (byte) 0x70;

                UDPXutil udpXutil=new UDPXutil();
                String serverHost = "10.10.10.134";
                //设置端口号
                int serverPort = 45452;
                udpXutil.send(serverHost, serverPort, b);
                //接收返回的结果
                String info = udpXutil.receive(serverHost, serverPort);
                Log.i("dandy",info);
                String[] res = info.split(" ");
                System.out.println(res[0]);
                double temp=getDoubleValue(res[1]);
                String tem=String.valueOf(temp);
                System.out.println(tem);
                Message msg  =   new  Message();
                Bundle bb = new Bundle();
                bb.putString("distance", res[0]);
                bb.putString("angle_horizontal", "angle_horizontal_value");  // 水平角度暂时没有
                bb.putString("angle_vertical", tem);

                msg.setData(bb);
                System.out.println(msg);
                receiveHandler.sendMessage(msg);

                udpXutil.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /*
     *   UDP数据接收线程
     * */
    public class UdpReceiveThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {

                receiveSocket = new DatagramSocket(RECEIVE_PORT);
                serverAddr = InetAddress.getByName(SEND_IP);

                while(listenStatus)
                {
                    byte[] inBuf= new byte[2];
                    DatagramPacket inPacket=new DatagramPacket(inBuf,inBuf.length);
                    receiveSocket.receive(inPacket);
                    System.out.println("!!!!!!#####");
                    if(!inPacket.getAddress().equals(serverAddr)){
                        Toast.makeText(PlayerActivity.this, "未知来源的报文", Toast.LENGTH_SHORT).show();
                        throw new IOException("未知名的报文");
                    }

                    receiveInfo = inPacket.getData();

                    String msgString = convertHexToString(formatHexString(receiveInfo, false));
                    // Logger.d(msgString);
                    String[] res = msgString.split(" ");
//                    Logger.d(res);
//                    Logger.d(res[0].getClass().toString());
//                    Logger.d(res[1]);

                    Message msg  =   new  Message();
                    Bundle b = new Bundle();
                    b.putString("distance", res[0]);
                    b.putString("angle_horizontal", "angle_horizontal_value");  // 水平角度暂时没有
                    b.putString("angle_vertical", res[1]);

                    msg.setData(b);
                    receiveHandler.sendMessage(msg);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    // 处理二进制数据
    public static String formatHexString(byte[] data, boolean addSpace) {
        if (data == null || data.length < 1)
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
            if (addSpace)
                sb.append(" ");
        }
        return sb.toString().trim();
    }


    // 16进制转化成ASCII码
    public static String convertHexToString(String hex) {
        if(hex == null){
            return "";
        }
        hex = hex.trim();
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        // 564e3a322d302e312e34 split into two characters 56, 4e, 3a...
        for (int i = 0; i < hex.length() - 1; i += 2) {
            // grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            // convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            // convert the decimal to character
            sb.append((char) decimal);
            temp.append(decimal);
        }
        // Logger.d(sb.toString());
        return sb.toString().trim();
    }


    private VideoView.OnStateChangeListener mOnStateChangeListener = new VideoView.SimpleOnStateChangeListener() {
        @Override
        public void onPlayerStateChanged(int playerState) {
            switch (playerState) {
                case VideoView.PLAYER_NORMAL://小屏
                    break;
                case VideoView.PLAYER_FULL_SCREEN://全屏
                    break;
            }
        }

        @Override
        public void onPlayStateChanged(int playState) {
            switch (playState) {
                case VideoView.STATE_IDLE:
                    break;
                case VideoView.STATE_PREPARING:
                    //在STATE_PREPARING时设置setMute(true)可实现静音播放
//                    mVideoView.setMute(true);
                    break;
                case VideoView.STATE_PREPARED:
                    break;
                case VideoView.STATE_PLAYING:
                    //需在此时获取视频宽高
                    int[] videoSize = mVideoView.getVideoSize();
                    L.d("视频宽：" + videoSize[0]);
                    L.d("视频高：" + videoSize[1]);
                    break;
                case VideoView.STATE_PAUSED:
                    Logger.d("暂停了");
                    mVideoView.start();
                    Logger.d("又开始了");
                    break;
                case VideoView.STATE_BUFFERING:
                    break;
                case VideoView.STATE_BUFFERED:
                    break;
                case VideoView.STATE_PLAYBACK_COMPLETED:
                    break;
                case VideoView.STATE_ERROR:
                    break;
            }
        }
    };

    private int i = 0;




    @Override
    public void onClick(View view) {
        int id = view.getId();
        System.out.println(id);
        switch (id) {
            case R.id.button:
                // 点击了设置告警距离按钮
                Logger.d("@@@@@@@@@@@");

                final EditText commandInput = new EditText(PlayerActivity.this);
                new AlertDialog.Builder(PlayerActivity.this)
                        .setTitle("请输入要设置的告警距离:")
                        .setView(commandInput)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    warningDistance = Float.parseFloat(commandInput.getText().toString());
                                    warningDistanceView.setText(warningDistance+"米");
                                    Toast.makeText(getApplicationContext(), "成功设置为：" + warningDistance , Toast.LENGTH_SHORT).show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "设置失败，当前告警距离为：" + warningDistance , Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(), "取消设置告警距离", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();


                break;

        }
    }



    public void onButtonClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.scale_default:
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT);
                break;
            case R.id.scale_169:
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_16_9);
                break;
            case R.id.scale_43:
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_4_3);
                break;
            case R.id.scale_original:
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_ORIGINAL);
                break;
            case R.id.scale_match_parent:
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT);
                break;
            case R.id.scale_center_crop:
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP);
                break;

            case R.id.speed_0_5:
                mVideoView.setSpeed(0.5f);
                break;
            case R.id.speed_0_75:
                mVideoView.setSpeed(0.75f);
                break;
            case R.id.speed_1_0:
                mVideoView.setSpeed(1.0f);
                break;
            case R.id.speed_1_5:
                mVideoView.setSpeed(1.5f);
                break;
            case R.id.speed_2_0:
                mVideoView.setSpeed(2.0f);
                break;

            case R.id.screen_shot:
                ImageView imageView = findViewById(R.id.iv_screen_shot);
                Bitmap bitmap = mVideoView.doScreenShot();
                imageView.setImageBitmap(bitmap);
                break;

            case R.id.mirror_rotate:
                mVideoView.setMirrorRotation(i % 2 == 0);
                i++;
                break;
            case R.id.btn_mute:
                mVideoView.setMute(true);
                break;
        }
    }
}
