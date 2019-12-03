package com.qmkj.niaogebiji.module.activity;

import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.google.android.material.tabs.TabLayout;
import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.BubbleGradient;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.adapter.ProfessionItemAdapter;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.ProBean;
import com.qmkj.niaogebiji.module.bean.TestBean;
import com.qmkj.niaogebiji.module.widget.GlideLoader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-02
 * 描述:
 */
public class MoreKnowYouActivity extends BaseActivity {

    @BindView(R.id.gif_pic)
    SimpleDraweeView gif_pic;

    @BindView(R.id.profession_picker)
    FrameLayout profession_picker;

    @BindView(R.id.part11)
    LinearLayout part11;

    @BindView(R.id.part22)
    LinearLayout part22;

    @BindView(R.id.part_picker)
    FrameLayout part_picker;

    @BindView(R.id.complete_part)
    RelativeLayout complete_part;

    @BindView(R.id.calcate_part)
    LinearLayout calcate_part;

    @BindView(R.id.picker)
    BubblePicker picker;

    @BindView(R.id.toNext)
    TextView toNext;

    Typeface mediumTypeface;

    Set<PickerItem> mSet = new HashSet<>();

    private String[] names = new String[]{"莘莘学子","初入职场","三年筑基","五年结丹","十年洞虚","其他"};
    private int[] images = new int[]{R.mipmap.icon_pic_1,R.mipmap.icon_pic_2,R.mipmap.icon_pic_3,
            R.mipmap.icon_pic_5,R.mipmap.icon_pic_10,R.mipmap.icon_pic_other};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_moreknowyou;
    }

    @Override
    protected void initView() {

        mediumTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_medium.ttf");
    }

    @Override
    public void initData() {
        getDynamicData();
        initLayout();
        getData();
    }

    private void getData() {
        ProBean proBean;
        for (int i = 0; i < images.length; i++) {
            proBean = new ProBean();
            proBean.setImg(images[i]);
            proBean.setName(names[i]);
            mAllList.add(proBean);
        }
        mProfessionItemAdapter.setNewData(mAllList);
    }


    private void getDynamicData() {


        final String[] titles = getResources().getStringArray(R.array.countries);
        final TypedArray colors = getResources().obtainTypedArray(R.array.colors);

        picker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return titles.length;
            }

            @NotNull
            @Override
            public PickerItem getItem(int position) {
                PickerItem item = new PickerItem();
                item.setTitle(titles[position]);
                item.setTypeface(mediumTypeface);
                item.setTextColor(ContextCompat.getColor(MoreKnowYouActivity.this, android.R.color.white));
                item.setColor(colors.getColor((position * 2) % 8,0));
                return item;
            }
        });

        colors.recycle();

        picker.setBubbleSize(20);

        picker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem item) {
                KLog.d("tag","选择 " + item.getTitle());
                mSet.add(item);
            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem item) {
                mSet.remove(item);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        picker.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        picker.onPause();
    }


    @OnClick({R.id.toNext,R.id.toCancle,
            R.id.toNext2,R.id.toCancle2,
            R.id.toComplete
    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.toNext2:
                part22.setVisibility(View.GONE);
                calcate_part.setVisibility(View.VISIBLE);
                String url = "https://raw.githubusercontent.com/Jay-YaoJie/KotlinDialogs/master/diagram/test.gif";

                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(Uri.parse(url))
                        .setAutoPlayAnimations(false)
                        .setControllerListener( new BaseControllerListener<ImageInfo> (){
                            @RequiresApi(api = Build.VERSION_CODES.P)
                            @Override
                            public void onFinalImageSet(String id, ImageInfo imageInfo,  Animatable animatable) {
                                if(animatable != null && !animatable.isRunning()){
                                    animatable.start();
                                    Animatable2 drawable = (Animatable2) animatable;
                                    drawable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                                        @Override
                                        public void onAnimationEnd(Drawable drawable) {
                                            super.onAnimationEnd(drawable);
                                            KLog.d("tag","gif 动画完成");
                                            //播放完成回调
                                            calcate_part.setVisibility(View.GONE);
                                            complete_part.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
                        })
                      .build();

                gif_pic.setController(controller);


                break;
            case R.id.toCancle2:
                part22.setVisibility(View.GONE);
                calcate_part.setVisibility(View.VISIBLE);
                break;
            case R.id.toComplete:
                finish();
            case R.id.toNext:
                KLog.d("tag","大小是 ： " + mSet.size() + "" );
                part11.setVisibility(View.GONE);
                part22.setVisibility(View.VISIBLE);
                break;

            case R.id.toCancle:
                part11.setVisibility(View.GONE);
                part22.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }




    /** --------------------------------- 通用的配置  ---------------------------------*/
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    ProfessionItemAdapter mProfessionItemAdapter;
    //组合集合
    List<ProBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mProfessionItemAdapter = new ProfessionItemAdapter(mAllList);
        mRecyclerView.setAdapter(mProfessionItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {
        //点击事件
        mProfessionItemAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","点击的是 position " + position );
            List<ProBean> mDatas = adapter.getData();
            //① 将所有的selected设置false，当前点击的设为true
            for (ProBean data : mDatas) {
                data.setSelect(false);
            }
            ProBean temp = mDatas.get(position);
            temp.setSelect(true);
            mProfessionItemAdapter.notifyDataSetChanged();
        });

    }



}
