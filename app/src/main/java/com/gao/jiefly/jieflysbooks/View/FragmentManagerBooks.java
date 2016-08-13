package com.gao.jiefly.jieflysbooks.View;

import android.content.DialogInterface;
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

import com.gao.jiefly.jieflysbooks.Model.AdvanceDataModel;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.R;
import com.gao.jiefly.jieflysbooks.Utils.AndroidUtilities;
import com.gao.jiefly.jieflysbooks.Utils.ApplicationLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class FragmentManagerBooks extends Fragment {
    @InjectView(R.id.id_scan_bottom_bar_left_btn)
    Button mIdScanBottomBarLeftBtn;
    //    @InjectView(R.id.id_scan_bottom_bar_right_btn)
//    Button mIdScanBottomBarRightBtn;
    @InjectView(R.id.id_scan_bottom_bar)
    LinearLayout mIdScanBottomBar;
    private View mView;
    private RecyclerView mRecyclerView;
    public List<Book> mFiles = new ArrayList<>();
    private CustomRecycleAdapter mCustomRecycleAdapter;
    private boolean isChooseAll = false;
    private List<Book> mRemoveBooks = new ArrayList<>();
    private AdvanceDataModel mAdvanceDataModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_manager, container, false);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.id_fragment_scan_rv);
        initRecycleView();
        ButterKnife.inject(this, mView);
        mAdvanceDataModel = AdvanceDataModel.build(ApplicationLoader.applicationContext);
        mFiles = mAdvanceDataModel.getBookList();
        if (mFiles !=null) {
            mCustomRecycleAdapter.notifyItemRangeInserted(0, mFiles.size());
        }else
            mFiles = new ArrayList<>();
        return mView;
    }


    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void addItem(Book book) {
        mFiles.add(book);
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
                mIdScanBottomBarLeftBtn.setText("删除" + (bookNum > 0 ? "(" + bookNum + ")" : ""));
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

    @OnClick({R.id.id_scan_bottom_bar_left_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_scan_bottom_bar_left_btn:
                List<Integer> position = mCustomRecycleAdapter.getCheckPositionList();
                if (position.size() < 1)
                    return;
                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle("删除书籍")
                        .setMessage(mFiles.get(mCustomRecycleAdapter.getCheckPositionList().get(0)).getBookName() + (mCustomRecycleAdapter.getCheckPositionList().size() > 1 ? ("等 " + mCustomRecycleAdapter.getCheckPositionList().size() + " 本书籍") : ""))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i : mCustomRecycleAdapter.getCheckPositionList()) {
                                    mRemoveBooks.add(mFiles.get(i));
                                }
                                mCustomRecycleAdapter.cancleItems();
                                for (Book book : mRemoveBooks) {
                                    mCustomRecycleAdapter.notifyItemRemoved(getBooksCurrentPosition(book));
                                    mFiles.remove(book);
                                }
                                int remainCount = mFiles.size() - mRemoveBooks.size();
//                                mCustomRecycleAdapter.notifyItemRangeRemoved(0,mRemoveBooks.size() - 1);
//                                mFiles.removeAll(mRemoveBooks);
                                if (remainCount > 0)
                                    mCustomRecycleAdapter.notifyItemRangeChanged(0, remainCount - 1);
//                                mCustomRecycleAdapter.cancleItems();
                                ((ScanTxtView) getActivity()).chooseFilesComplete(null);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
        }
    }

    private int getBooksCurrentPosition(Book book) {
        for (int i = 0; i < mFiles.size(); i++) {
            if (mFiles.get(i).equals(book))
                return i;
        }
        return 0;
    }

    interface OnDataChangeListener {
        void OnDataChange();
    }

    public void toogleChooseAll() {
        if (!isChooseAll) {
            isChooseAll = true;
//            mIdScanBottomBarRightBtn.setText("取消");
            mCustomRecycleAdapter.chooseAllItems();
        } else {
            isChooseAll = false;
//            mIdScanBottomBarRightBtn.setText("全选");
            mCustomRecycleAdapter.cancleItems();
        }
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
            notifyItemRangeChanged(0, mFiles.size());
            mListener.OnDataChange();
        }

        public void cancleItems() {
            checkPositionList.clear();
            notifyItemRangeChanged(0, mFiles.size());
            mListener.OnDataChange();
        }

        public void setOnDataChangeListener(OnDataChangeListener listener) {
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FragmentManagerBooks.CustomRecycleAdapter.ViewHolder holder = new ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_scan, parent, false));

            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.txt_icon));
            String name = mFiles.get(position).getBookName();
            holder.mNameTV.setText(name);
            holder.mSizeTV.setText(mFiles.get(position).getBookNewTopicTitle());
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
