package me.nereo.imagechoose.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.imagechoose.ShowActivity;
import me.nereo.imagechoose.bean.Image;
import me.nereo.imagechoose.utils.FileUtils;
import me.nereo.multi_image_selector.R;
import song.image.crop.Crop;
import song.image.crop.HDApp;

/**
 * 图片Adapter
 * Created by Nereo on 2015/4/7.
 */
public class ImageGridAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_TXT = 1;
    private static final int TYPE_NORMAL = 2;

    private Context mContext;

    private LayoutInflater mInflater;
    private boolean showCamera = true;
    private boolean showTxt = false;
    private boolean showSelectIndicator = true;

    private List<Image> mImages = new ArrayList<>();
    private List<Image> mSelectedImages = new ArrayList<>();

    private int mItemSize;
    private GridView.LayoutParams mItemLayoutParams;

    public ImageGridAdapter(Context context, boolean showCamera,boolean showTxt){
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.showCamera = showCamera;
        this.showTxt = showTxt;
        mItemLayoutParams = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT);
    }
    /**
     * 显示选择指示器
     * @param b
     */
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    public void setShowCamera(boolean b){
        if(showCamera == b) return;

        showCamera = b;
        notifyDataSetChanged();
    }
    public void setShowTxt(boolean b){
        if(showTxt == b) return;

        showTxt = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera(){
        return showCamera;
    }
    public boolean isShowTxt(){
        return showTxt;
    }

    /**
     * 选择某个图片，改变选择状态
     * @param image
     */
    public void select(Image image) {
        if(mSelectedImages.contains(image)){
            mSelectedImages.remove(image);
        }else{
            mSelectedImages.add(image);
        }
        notifyDataSetChanged();
    }

    /**
     * 通过图片路径设置默认选择
     * @param resultList
     */
    public void setDefaultSelected(ArrayList<String> resultList) {
        for(String path : resultList){
            Image image = getImageByPath(path);
            if(image != null){
                mSelectedImages.add(image);
            }
        }
        if(mSelectedImages.size() > 0){
            notifyDataSetChanged();
        }
    }

    private Image getImageByPath(String path){
        if(mImages != null && mImages.size()>0){
            for(Image image : mImages){
                if(image.path.equalsIgnoreCase(path)){
                    return image;
                }
            }
        }
        return null;
    }

    /**
     * 设置数据集
     * @param images
     */
    public void setData(List<Image> images) {
        mSelectedImages.clear();

        if(images != null && images.size()>0){
            mImages = images;
        }else{
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 重置每个Column的Size
     * @param columnWidth
     */
    public void setItemSize(int columnWidth) {

        if(mItemSize == columnWidth){
            return;
        }

        mItemSize = columnWidth;

        mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);

        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if(showCamera){
            if(position == 0){
                return TYPE_CAMERA;
            }
        }
        if(isShowTxt()){
            if(showCamera){
                if(position == 1){
                    return TYPE_TXT;
                }
            }else{
                if(position == 0){
                    return TYPE_TXT;
                }
            }
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        if(showCamera && showTxt){
            return mImages.size()+2;
        }else if(showCamera || showTxt){
            return mImages.size()+1;
        }

        return mImages.size();
    }

    @Override
    public Image getItem(int i) {
        if(showCamera && showTxt){
            if(i == 0){
                return null;
            }
            if(i == 1){
                return null;
            }
            return mImages.get(i-2);
        }else if(showCamera || showTxt){
            if(i == 0){
                return null;
            }
            return mImages.get(i-1);
        }else{
            return mImages.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        int type = getItemViewType(i);
        if(type == TYPE_CAMERA){
            view = mInflater.inflate(R.layout.choose_list_item_camera, viewGroup, false);
            view.setTag(null);
        }else if(type == TYPE_TXT){
            view = mInflater.inflate(R.layout.choose_list_item_text, viewGroup, false);
            view.setTag(null);
        } else if(type == TYPE_NORMAL){
            ViewHolde holde;
            if(view == null){
                view = mInflater.inflate(R.layout.choose_list_item_image, viewGroup, false);
                holde = new ViewHolde(view);
            }else{
                holde = (ViewHolde) view.getTag();
                if(holde == null){
                    view = mInflater.inflate(R.layout.choose_list_item_image, viewGroup, false);
                    holde = new ViewHolde(view);
                }
            }

            if(holde != null) {
                holde.bindData(getItem(i));

                final Image image = getItem(i);
                if(showSelectIndicator){//多选模式
                    holde.iv_choose_area.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent showIntent =new Intent(mContext, ShowActivity.class);
                            showIntent.putExtra("path",image.path);
                            mContext.startActivity(showIntent);
                        }
                    });
                }else{//单张裁切
                    holde.iv_choose_area.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           File tmpFile= FileUtils.createTmpFile(mContext);
                            HDApp.getInstance().setSingleChooseFile(tmpFile);
                            new Crop(Uri.fromFile(new File(image.path)))
                                    .output(Uri.fromFile(tmpFile))
                                    .withWidth(640)
                                    .start((Activity) mContext);

                            ((Activity) mContext).finish();
                        }
                    });
                }
            }
        }

        /** Fixed View Size */
        GridView.LayoutParams lp = (GridView.LayoutParams) view.getLayoutParams();
        if(lp.height != mItemSize){
            view.setLayoutParams(mItemLayoutParams);
        }

        return view;
    }

    class ViewHolde {
        ImageView image;
        ImageView indicator;
        View mask;
        ImageView iv_choose_area;

        ViewHolde(View view){
            image = (ImageView) view.findViewById(R.id.image);
            indicator = (ImageView) view.findViewById(R.id.checkmark);
            mask = view.findViewById(R.id.mask);
            iv_choose_area = (ImageView) view.findViewById(R.id.iv_choose_area);
            view.setTag(this);
        }

        void bindData(final Image data){
            if(data == null) return;
            // 处理单选和多选状态
            if(showSelectIndicator){
                indicator.setVisibility(View.VISIBLE);
                if(mSelectedImages.contains(data)){
                    // 设置选中状态
                    indicator.setImageResource(R.drawable.btn_selected);
                    mask.setVisibility(View.VISIBLE);
                }else{
                    // 未选择
                    indicator.setImageResource(R.drawable.btn_unselected);
                    mask.setVisibility(View.GONE);
                }
            }else{
                indicator.setVisibility(View.GONE);
            }
            File imageFile = new File(data.path);

            if(mItemSize > 0) {
                // 显示图片
//                Picasso.with(mContext).setDebugging(true);
                Picasso.with(mContext)
                        .load(imageFile)
                        .placeholder(R.drawable.default_error)
                                //.error(R.drawable.default_error)
                        .resize(mItemSize, mItemSize)
                        .centerCrop()
                        .into(image);
            }
        }
    }

}
