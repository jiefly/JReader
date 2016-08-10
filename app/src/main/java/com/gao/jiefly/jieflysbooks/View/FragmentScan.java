package com.gao.jiefly.jieflysbooks.View;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gao.jiefly.jieflysbooks.R;
import com.gao.jiefly.jieflysbooks.Utils.AndroidUtilities;
import com.gao.jiefly.jieflysbooks.Utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class FragmentScan extends Fragment {
    @InjectView(R.id.id_scan_bottom_bar_left_btn)
    Button mIdScanBottomBarLeftBtn;
    @InjectView(R.id.id_scan_bottom_bar_right_btn)
    Button mIdScanBottomBarRightBtn;
    @InjectView(R.id.id_scan_bottom_bar)
    LinearLayout mIdScanBottomBar;
    private View mView;
    private RecyclerView mRecyclerView;
    public List<File> mFiles = new ArrayList<>();
    private CustomRecycleAdapter mCustomRecycleAdapter;
    private boolean isChooseAll = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_scan, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.id_fragment_scan_rv);
        initRecycleView();
        ButterKnife.inject(this, mView);
        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((ScanTxtView)getActivity()).hideScanTv(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ScanTxtView)getActivity()).hideScanTv(true);
    }

    public void addItem(File file) {
        mFiles.add(file);
        Observable.just(mFiles.size() - 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mCustomRecycleAdapter.notifyItemInserted(integer);
                    }
                });
    }

    private void initRecycleView() {
        final RecyclerView.LayoutManager manager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mCustomRecycleAdapter = new CustomRecycleAdapter();
        mCustomRecycleAdapter.setOnDataChangeListener(new OnDataChangeListener() {
            @Override
            public void OnDataChange() {
                int bookNum = mCustomRecycleAdapter.getCheckPositionList().size();
                mIdScanBottomBarLeftBtn.setText("收藏" + (bookNum > 0 ? "(" + bookNum + ")" : ""));
            }
        });
        mRecyclerView.setAdapter(mCustomRecycleAdapter);
        mRecyclerView.setVerticalScrollBarEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.id_scan_bottom_bar_left_btn, R.id.id_scan_bottom_bar_right_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_scan_bottom_bar_left_btn:
                break;
            case R.id.id_scan_bottom_bar_right_btn:
                if (!isChooseAll) {
                    isChooseAll = true;
                    mIdScanBottomBarRightBtn.setText("取消");
                    mCustomRecycleAdapter.chooseAllItems();
                } else {
                    isChooseAll = false;
                    mIdScanBottomBarRightBtn.setText("全选");
                    mCustomRecycleAdapter.cancleItems();
                }
                break;
        }
    }

    interface OnDataChangeListener {
        void OnDataChange();
    }

    class CustomRecycleAdapter extends RecyclerView.Adapter<CustomRecycleAdapter.ViewHolder> {
        public List<Integer> getCheckPositionList() {
            return checkPositionList;
        }

        List<Integer> checkPositionList = new ArrayList<>();
        private OnDataChangeListener mListener = null;

        public void chooseAllItems() {
            for (int i = 0; i < mFiles.size(); i++) {
                if (!checkPositionList.contains(i)) {
                    checkPositionList.add(i);
                }
            }
            notifyItemRangeChanged(0, mFiles.size() - 1);
            mListener.OnDataChange();
        }

        public void cancleItems() {
            checkPositionList.clear();
            notifyItemRangeChanged(0, mFiles.size() - 1);
            mListener.OnDataChange();
        }

        public void setOnDataChangeListener(OnDataChangeListener listener) {
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_scan, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.txt_icon));
            String name = mFiles.get(position).getName();
            holder.mNameTV.setText(name);
            holder.mSizeTV.setText(Utils.formatFileSize(mFiles.get(position).length()));
            holder.mRadioButton.setTag(position);
            if (checkPositionList != null) {
                holder.mRadioButton.setChecked((checkPositionList.contains(position)));
            } else {
                holder.mRadioButton.setChecked(false);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mRadioButton.setChecked(!holder.mRadioButton.isChecked());
                }
            });

            holder.mRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (!checkPositionList.contains(holder.mRadioButton.getTag())) {
                            checkPositionList.add(position);
                            mListener.OnDataChange();
                        }
                    } else {
                        if (checkPositionList.contains(holder.mRadioButton.getTag())) {
                            checkPositionList.remove(holder.mRadioButton.getTag());
                            mListener.OnDataChange();
                        }
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return mFiles.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView mImageView;
            CheckBox mRadioButton;
            TextView mNameTV;
            TextView mSizeTV;

            public ViewHolder(View itemView) {
                super(itemView);
                mImageView = (ImageView) itemView.findViewById(R.id.id_scan_item_icon);
                mRadioButton = (CheckBox) itemView.findViewById(R.id.id_scan_item_rbtn);
                mNameTV = (TextView) itemView.findViewById(R.id.id_scan_item_txt_name);
                mNameTV.setMaxWidth(AndroidUtilities.displaySize.x * 2 / 3);
                mSizeTV = (TextView) itemView.findViewById(R.id.id_scan_item_txt_size);
            }
        }
    }
}
