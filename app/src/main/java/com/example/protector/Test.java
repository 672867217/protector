package com.example.protector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.protector.SQl.Operator;
import com.example.protector.SQl.ProductType;
import com.example.protector.SQl.TestData;
import com.example.protector.SQl.XiuGai;
import com.example.protector.util.MyDialog;
import com.example.protector.util.Utils;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Test extends AppCompatActivity implements View.OnClickListener {

    private TextView header_tv;
    private TextView header_tv2;
    private TextView header_tv3;
    private TextView header_tv4;
    private Button test_btn;
    private GridView test_gv1;
    private GridView test_gv2;
    TestGv1ItemAdapter testGv1ItemAdapter;
    TestGv2ItemAdapter testGv2ItemAdapter;
    private TextView stats_tv3;
    private Spinner chanpin_spinner5;
    private TextView stats_tv2;
    private Spinner chanpin_spinner4;
    private Spinner chanpin_spinner2;
    private Spinner chanpin_spinner3;
    private Spinner chanpin_spinner;
    private TextView stats_tv1;
    String[] sp_name = {"其他测程","一测", "二测", "三测"};
    String[] sp_save = {"自动", "手动"};
    List sp_ceshi = new ArrayList() ;
    List sp_chanpin  = new ArrayList() ;
    List sp_shengchan = new ArrayList() ;
    String[] gv1_name2 = {"一测", "二测", "三测","其他"};
    List<Bean> list1 = new ArrayList();
    List<TestData> list2 = new ArrayList();
    boolean saveMode ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
        new Utils().hideNavKey(Test.this);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        stats_tv2.setText(dateFormat.format(new Date()));

        //数据库数据
        final List<TestData> dataList = DataSupport.findAll(TestData.class);

        //如果是新班次
        int what = getIntent().getIntExtra("what", -1);
        if (what == 1) {
            for (int i = 0; i < 5; i++) {
                Bean bean = new Bean();
                bean.ceshi = String.valueOf(0);
                bean.tongguo = String.valueOf(0);
                bean.weitongguo = String.valueOf(0);
                bean.yongshi = "0";
                list1.add(bean);
            }
        } else {
            for (int i = 1; i < 5; i++) {
                int shuliang=0,zongshijian=0;
                String num = String.valueOf(i);
                if (i == 4) {
                    num = 0 + "";
                }
                for (int j = 0; j < dataList.size(); j++) {
                    if (dataList.get(j).getCecheng().equals(num)) {
                        shuliang++;
                        zongshijian = Integer.parseInt(dataList.get(i).getCeshishichang());
                    }
                }
                Bean bean = new Bean();
                bean.ceshi = String.valueOf(shuliang);
                bean.tongguo = String.valueOf(shuliang);
                bean.weitongguo = String.valueOf(shuliang);
                bean.yongshi = zongshijian/60+"'"+zongshijian%60+"\"";
                list1.add(bean);
            }
        }


        testGv1ItemAdapter = new TestGv1ItemAdapter(this, list1);
        test_gv1.setAdapter(testGv1ItemAdapter);
        testGv1ItemAdapter.notifyDataSetChanged();

        testGv2ItemAdapter = new TestGv2ItemAdapter(this, list2);
        test_gv2.setAdapter(testGv2ItemAdapter);
        testGv2ItemAdapter.notifyDataSetChanged();
        final List<ProductType> types = DataSupport.findAll(ProductType.class);
        List<Operator> operators = DataSupport.findAll(Operator.class);

        for (int i = 0; i < types.size(); i++) {
            sp_chanpin.add(types.get(i).getName());
            sp_shengchan.add(types.get(i).getChangjia());
        }
        for (int i = 0; i < operators.size(); i++) {
            sp_ceshi.add(operators.get(i).getName());
        }
        ArrayAdapter nameAdapter = new ArrayAdapter(Test.this, android.R.layout.simple_spinner_dropdown_item, sp_name);
        ArrayAdapter ceshiAdapter = new ArrayAdapter(Test.this, android.R.layout.simple_spinner_dropdown_item,sp_ceshi);
        ArrayAdapter chanpinAdapter = new ArrayAdapter(Test.this, android.R.layout.simple_spinner_dropdown_item, sp_chanpin);
        ArrayAdapter saveAdapter = new ArrayAdapter(Test.this, android.R.layout.simple_spinner_dropdown_item, sp_save);

        chanpin_spinner.setAdapter(chanpinAdapter);

        chanpin_spinner3.setAdapter(nameAdapter);
        chanpin_spinner4.setAdapter(saveAdapter);
        chanpin_spinner5.setAdapter(ceshiAdapter);
        chanpin_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(types.size() != 0){
                    stats_tv1.setText(types.get(i).getXinghao());
                    ArrayAdapter shengchanAdapter = new ArrayAdapter(Test.this, android.R.layout.simple_spinner_dropdown_item, new String[]{sp_shengchan.get(i)+""});
                    chanpin_spinner2.setAdapter(shengchanAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        chanpin_spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                list2.clear();
                //当测程改变  通过串口向测试台主机发送信息
                switch (position) {
                    case 0:
                        for (int i = 0; i < 5; i++) {
                            if (dataList.get(i).getCecheng().equals("0")) {
                                list2.add(dataList.get(i));
                            }
                        }
                        break;
                    case 1:
                        for (int i = 0; i < 5; i++) {
                            if (dataList.get(i).getCecheng().equals("1")) {
                                list2.add(dataList.get(i));
                            }
                        }
                        break;
                    case 2:
                        for (int i = 0; i < 5; i++) {
                            if (dataList.get(i).getCecheng().equals("2")) {
                                list2.add(dataList.get(i));
                            }
                        }
                        break;
                    case 3:
                        for (int i = 0; i < 5; i++) {
                            if (dataList.get(i).getCecheng().equals("3")) {
                                list2.add(dataList.get(i));
                            }
                        }
                        break;
                }

                //选完以后更新适配器
                testGv2ItemAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        chanpin_spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    saveMode = true;
                    //自动模式 存数据
//                    TestData testData = new TestData();
//                    testData.save();
                } else {
                    saveMode = false;
                }
                testGv2ItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initView() {
        header_tv = (TextView) findViewById(R.id.header_tv);
        header_tv2 = (TextView) findViewById(R.id.header_tv2);
        header_tv3 = (TextView) findViewById(R.id.header_tv3);
        header_tv4 = (TextView) findViewById(R.id.header_tv4);
        test_btn = (Button) findViewById(R.id.test_btn);
        test_gv1 = (GridView) findViewById(R.id.test_gv1);
        test_gv2 = (GridView) findViewById(R.id.test_gv2);
        stats_tv3 = (TextView) findViewById(R.id.stats_tv3);
        stats_tv2 = (TextView) findViewById(R.id.stats_tv2);
        stats_tv1 = (TextView) findViewById(R.id.stats_tv1);
        chanpin_spinner5 = (Spinner) findViewById(R.id.chanpin_spinner5);
        chanpin_spinner4 = (Spinner) findViewById(R.id.chanpin_spinner4);
        chanpin_spinner2 = (Spinner) findViewById(R.id.chanpin_spinner2);
        chanpin_spinner3 = (Spinner) findViewById(R.id.chanpin_spinner3);
        chanpin_spinner = (Spinner) findViewById(R.id.chanpin_spinner);
        stats_tv1 = (TextView) findViewById(R.id.stats_tv1);
        test_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_btn:
                finish();
                break;
        }
    }

    class Bean{
        String ceshi,tongguo,weitongguo,yongshi;
    }

    public class TestGv1ItemAdapter extends BaseAdapter {

        private List<Bean> objects = new ArrayList<Bean>();

        private Context context;
        private LayoutInflater layoutInflater;

        public TestGv1ItemAdapter(Context context, List<Bean> objects) {
            this.context = context;
            this.objects = objects;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public Bean getItem(int position) {
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.test_gv1_item, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            initializeViews(getItem(position), (ViewHolder) convertView.getTag(),position);
            return convertView;
        }

        private void initializeViews(Bean bean1, ViewHolder holder,int what) {
            //TODO implement
            holder.testItem1Tv.setText(gv1_name2[what]+"统计");
            holder.testItem1Tv1.setText(bean1.ceshi);
            holder.testItem1Tv2.setText(bean1.tongguo);
            holder.testItem1Tv3.setText(bean1.weitongguo);
            holder.testItem1Tv4.setText(bean1.yongshi);

        }

        protected class ViewHolder {
            private TextView testItem1Tv;
            private TextView testItem1Tv1;
            private TextView testItem1Tv2;
            private TextView testItem1Tv3;
            private TextView testItem1Tv4;

            public ViewHolder(View view) {
                testItem1Tv = (TextView) view.findViewById(R.id.test_item1_tv);
                testItem1Tv1 = (TextView) view.findViewById(R.id.test_item1_tv1);
                testItem1Tv2 = (TextView) view.findViewById(R.id.test_item1_tv2);
                testItem1Tv3 = (TextView) view.findViewById(R.id.test_item1_tv3);
                testItem1Tv4 = (TextView) view.findViewById(R.id.test_item1_tv4);
            }
        }
    }

    public class TestGv2ItemAdapter extends BaseAdapter {

        private List<TestData> objects = new ArrayList<TestData>();

        private Context context;
        private LayoutInflater layoutInflater;

        public TestGv2ItemAdapter(Context context, List<TestData> objects) {
            this.context = context;
            this.objects = objects;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public TestData getItem(int position) {
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.test_gv2_item, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            initializeViews(getItem(position), (ViewHolder) convertView.getTag(),position);
            return convertView;
        }

        private void initializeViews(TestData testData, ViewHolder holder,int what) {
            //TODO implement

            holder.testItem2Tv.setText("工位"+(what+1));
            holder.testItem2Tv1.setText("自动");
            holder.testItem2Tv2.setText(testData.getChanpinbianma());
            holder.testItem2Tv3.setText(testData.getChanpinbianma());
            holder.testItem2Tv4.setText(testData.getCecheng() );
            if (Integer.parseInt(testData.getCeshishichang()) < 60) {
                holder.testItem2Tv5.setText(testData.getCeshishichang()+"s");
            } else {
                holder.testItem2Tv5.setText(Integer.parseInt(testData.getCeshishichang())/60+"\""
                        +Integer.parseInt(testData.getCeshishichang())%60+"s");
            }
            //测试结果
            holder.testItem2Tv8.setText(testData.getQidongshijian());
            holder.testItem2Tv9.setText(testData.getM13xianshishijian());
            holder.testItem2Tv10.setText(testData.getM30xianshishijian());
            holder.testItem2Tv11.setText(testData.getXianquanchuanlian5());
            holder.testItem2Tv12.setText(testData.getXianquanbinglian());
            holder.testItem2Tv16.setText("无");
            holder.testItem2Tv17.setText("合格");

            int[] arr = new int[3];
            double[] arr2 = new double[3];
            double[] arr3 = new double[3];
            int[] arr4 = new int[9];
            arr[0] = Integer.parseInt(testData.getAduanxiangxiangying());
            arr[1] = Integer.parseInt(testData.getBduanxiangxiangying());
            arr[2] = Integer.parseInt(testData.getCduanxiangxiangying());

            arr2[0] = Double.parseDouble(testData.getAduanxiangdianya());
            arr2[1] = Double.parseDouble(testData.getBduanxiangdianya());
            arr2[2] = Double.parseDouble(testData.getCduanxiangdianya());

            arr3[0] = Double.parseDouble(testData.getAxiangceyajiang());
            arr3[1] = Double.parseDouble(testData.getBxiangceyajiang());
            arr3[2] = Double.parseDouble(testData.getCxiangceyajiang());

            arr4[0] = Integer.parseInt(testData.getAbxiangjianjueyuan());
            arr4[1] = Integer.parseInt(testData.getAcxiangjianjueyuan());
            arr4[2] = Integer.parseInt(testData.getBcxiangjianjueyuan());
            arr4[3] = Integer.parseInt(testData.getAxiangduidijueyuan());
            arr4[4] = Integer.parseInt(testData.getBxiangduidijueyuan());
            arr4[5] = Integer.parseInt(testData.getCxiangduidijueyuan());
            arr4[6] = Integer.parseInt(testData.getAxiangduixianquanjueyuan());
            arr4[7] = Integer.parseInt(testData.getBxiangduixianquanjueyuan());
            arr4[8] = Integer.parseInt(testData.getCxiangduixianquanjeuyuan());

            Arrays.sort(arr); //断相响应时间
            Arrays.sort(arr2); //断相电压
            Arrays.sort(arr3); //线圈压降
            Arrays.sort(arr4); //绝缘电阻

            holder.testItem2Tv7.setText(arr[arr.length-1]+"");
            holder.testItem2Tv13.setText(arr2[arr2.length-1]+"");
            holder.testItem2Tv14.setText(arr3[arr3.length-1]+"");
            holder.testItem2Tv15.setText(arr4[0]+"");

//            List<XiuGai> biaozhun = DataSupport.findAll(XiuGai.class);
            //参数是否合格是根据标准参数判断 还是根据生产参数判断  无法确定
//            if (Integer.parseInt(holder.testItem2Tv7.getText().toString()) >= 150
//                    || Integer.parseInt(holder.testItem2Tv8.getText().toString()) >= 250
//                    || Double.parseDouble(holder.testItem2Tv9.getText().toString()) > 14
//                    || Double.parseDouble(holder.testItem2Tv9.getText().toString()) < 12
//                    || Double.parseDouble(holder.testItem2Tv10.getText().toString()) > 31
//                    || Double.parseDouble(holder.testItem2Tv10.getText().toString()) < 29
//                    || Double.parseDouble(holder.testItem2Tv11.getText().toString()) < 20
//                    || Double.parseDouble(holder.testItem2Tv11.getText().toString()) > 27.5
//                    || Double.parseDouble(holder.testItem2Tv12.getText().toString()) < 10
//                    || Double.parseDouble(holder.testItem2Tv12.getText().toString()) > 14
//                    || Double.parseDouble(holder.testItem2Tv13.getText().toString()) > 0.2
//                    || Double.parseDouble(holder.testItem2Tv14.getText().toString()) > 3
//                    || Integer.parseInt(holder.testItem2Tv15.getText().toString()) < 500) {
//
//            }

            //点击保存弹出dialog 1秒后自动关闭
            holder.testBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = LayoutInflater.from(Test.this).inflate(R.layout.dialog_test4, null);
                    final MyDialog dialog = new MyDialog(Test.this, view, R.style.dialog);
                    dialog.show();
                    final Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            //数据保存到数据库
//                            TestData testData = new TestData();
//                            testData.save();
                            dialog.dismiss();
                            timer.cancel();
                        }
                    };
                    timer.schedule(timerTask,1000,200);
                }
            });

            //保存模式为自动不可用 手动可用
            if (saveMode) {
                holder.testBtn2.setBackgroundResource(R.drawable.queding);
                holder.testBtn2.setEnabled(false);
            }else {
                holder.testBtn2.setBackgroundResource(R.drawable.dayinweixiuqindan);
                holder.testBtn2.setEnabled(true);
            }
            //最后一个工位不工作 灰色
            if (what == 4) {
                holder.testBtn2.setBackgroundResource(R.drawable.queding);
                holder.testBtn3.setBackgroundResource(R.drawable.queding);
                holder.testBtn4.setBackgroundResource(R.drawable.queding);
                holder.testItem2Tv.setBackgroundResource(R.drawable.huiseshangyuanjiao);

                holder.testBtn2.setEnabled(false);
                holder.testBtn3.setEnabled(false);
                holder.testBtn4.setEnabled(false);
                holder.gv2_layout.setEnabled(false);
            }

        }

        protected class ViewHolder {
            private TextView testItem2Tv;
            private TextView testItem2Tv1;
            private EditText testItem2Tv2;
            private TextView testItem2Tv3;
            private TextView testItem2Tv4;
            private TextView testItem2Tv5;
            private TextView testItem2Tv6;
            private TextView testItem2Tv7;
            private TextView testItem2Tv8;
            private TextView testItem2Tv9;
            private TextView testItem2Tv10;
            private TextView testItem2Tv11;
            private TextView testItem2Tv12;
            private TextView testItem2Tv13;
            private TextView testItem2Tv14;
            private TextView testItem2Tv15;
            private TextView testItem2Tv16;
            private TextView testItem2Tv17;
            private Button testBtn4;
            private Button testBtn3;
            private Button testBtn2;
            private LinearLayout gv2_layout;

            public ViewHolder(View view) {
                testItem2Tv = (TextView) view.findViewById(R.id.test_item2_tv);
                testItem2Tv1 = (TextView) view.findViewById(R.id.test_item2_tv1);
                testItem2Tv2 = (EditText) view.findViewById(R.id.test_item2_tv2);
                testItem2Tv3 = (TextView) view.findViewById(R.id.test_item2_tv3);
                testItem2Tv4 = (TextView) view.findViewById(R.id.test_item2_tv4);
                testItem2Tv5 = (TextView) view.findViewById(R.id.test_item2_tv5);
//                testItem2Tv6 = (TextView) view.findViewById(R.id.test_item2_tv6);
                testItem2Tv7 = (TextView) view.findViewById(R.id.test_item2_tv7);
                testItem2Tv8 = (TextView) view.findViewById(R.id.test_item2_tv8);
                testItem2Tv9 = (TextView) view.findViewById(R.id.test_item2_tv9);
                testItem2Tv10 = (TextView) view.findViewById(R.id.test_item2_tv10);
                testItem2Tv11 = (TextView) view.findViewById(R.id.test_item2_tv11);
                testItem2Tv12 = (TextView) view.findViewById(R.id.test_item2_tv12);
                testItem2Tv13 = (TextView) view.findViewById(R.id.test_item2_tv13);
                testItem2Tv14 = (TextView) view.findViewById(R.id.test_item2_tv14);
                testItem2Tv15 = (TextView) view.findViewById(R.id.test_item2_tv15);
                testItem2Tv16 = (TextView) view.findViewById(R.id.test_item2_tv16);
                testItem2Tv17 = (TextView) view.findViewById(R.id.test_item2_tv17);
                testBtn4 = (Button) view.findViewById(R.id.test_btn4);
                testBtn3 = (Button) view.findViewById(R.id.test_btn3);
                testBtn2 = (Button) view.findViewById(R.id.test_btn2);
                gv2_layout = view.findViewById(R.id.gv2_layout);
            }
        }
    }

}
