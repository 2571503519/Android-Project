package com.zjl.criminalintent;

import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zjl.criminalintent.domain.Crime;
import com.zjl.criminalintent.domain.CrimeLab;

import java.util.List;

/**
 * Created by lenovo on 2017/8/8.
 */
public class CrimeListFragment extends Fragment {
    private static final int REQUEST_CODE = 1;
    public static final String POSITION = "position";
    private RecyclerView mRecyclerView;
    private TextView mNoCrimeTextView;
    private CrimeAdapter mAdapter;
    private int mPosition;
    private boolean mSubTitleVisible;
    private Callbacks mCallbacks;

    private final static String TAG = "CrimeListFragment";
    private static final String SAVED_SUBTITLE_VISIBLE = "subTitle";

    public interface Callbacks{
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_crime);
        mNoCrimeTextView = (TextView) view.findViewById(R.id.tv_crime_no_crime);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null){
            mSubTitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI(0);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubTitleVisible);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI(mPosition);
    }

    public void updateUI(int position) {
        //获取数据池中的数据
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        //创建适配器，并设置适配器
        if (mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mRecyclerView.setAdapter(mAdapter);
            ItemTouchHelper.Callback callback = new MyCallback(mAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(mRecyclerView);
        }else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
//            mAdapter.notifyItemChanged(position);
        }

        updateSubTitle();
        if(CrimeLab.get(getActivity()).getCrimes().size() != 0){
            mNoCrimeTextView.setVisibility(View.GONE);
        } else {
            mNoCrimeTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK){
                    updateUI(data.getIntExtra(POSITION, 0));
                }
                break;
            default:
                break;
        }
    }

    public interface ItemTouchHelperAdapter{
        void onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }

    /**
     * 不包含 police.png 图标
     */
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mImageView;
        private Crime mCrime;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_crime, parent, false));

            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.tv_crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.tv_crime_date);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_image);
        }

        @Override
        public void onClick(View v) {
//            intent.putExtra(POSITION, mPosition);
//            Log.d(TAG, "onClick " + mPosition);
            //获取子view 在RecyclerView 中的位置
            mPosition = mRecyclerView.getChildAdapterPosition(v);
            mCallbacks.onCrimeSelected(mCrime);
//              startActivityForResult(intent, REQUEST_CODE);
        }

        public void bind(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(DateFormat.format("MMM dd, yyyy h:mmaa", mCrime.getDate()));
            mImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * 包含 police.png 图标，与上一个的区别只有加载的布局文件不同
     */
    private class CrimePoliceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //标题
        private TextView mTitleTextView;
        //日期
        private TextView mDateTextView;
        //数据
        private Crime mCrime;
        //图片
        private ImageView mImageView;

        public CrimePoliceHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_crime_police, parent, false));

            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.tv_crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.tv_crime_date);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_image);

        }

        /**
         * 为每一个item添加点击事件
         * @param v
         */
        @Override
        public void onClick(View v) {
//            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
//            intent.putExtra(POSITION, mPosition);
//            Log.d(TAG, "onClick " + mPosition);
            mPosition = mRecyclerView.getChildAdapterPosition(v);
            mCallbacks.onCrimeSelected(mCrime);
//            startActivity(intent);
//            startActivityForResult(intent, REQUEST_CODE);
        }

        /**
         * 绑定数据，根据mCrime 来显示视图
         * @param crime
         */
        public void bind(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(DateFormat.format("MMM dd, yyyy h:mmaa", mCrime.getDate()));
            mImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperAdapter{
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //根据viewType的值，选择创建不同类型的ViewHolder
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            if(viewType == 1){
                return new CrimePoliceHolder(layoutInflater, parent);
            }else {
                return new CrimeHolder(layoutInflater, parent);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Crime crime = mCrimes.get(position);

            //判断holder的类型，来选择调用不同类中的方法
            if( holder instanceof CrimeHolder){
                ((CrimeHolder) holder).bind(crime);
            }else if (holder instanceof CrimePoliceHolder){
                ((CrimePoliceHolder) holder).bind(crime);
            }

        }


        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }

        /**
         * 设置不同位置上itemView 的ViewType 属性，在onCreateViewHolder（）之前调用
         * @param position
         * @return
         */
        @Override
        public int getItemViewType(int position) {
            if(mCrimes.get(position).isRequiresPolice()){
                return 1;
            }else{
                return 0;
            }
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {

        }

        @Override
        public void onItemDismiss(int position) {
            Log.d(TAG, "onItemDismiss " + position);
            Crime crime = mCrimes.get(position);
            CrimeLab.get(getActivity()).removeCrime(crime);
            updateUI(position);
        }
    }

    private class MyCallback extends ItemTouchHelper.Callback{
        private ItemTouchHelperAdapter mAdapter;

        public MyCallback(ItemTouchHelperAdapter adapter){
            mAdapter = adapter;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.LEFT;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subTitleItem = menu.findItem(R.id.show_subTitle);
        if (mSubTitleVisible){
            subTitleItem.setTitle(R.string.hide_subTitle);
        } else {
            subTitleItem.setTitle(R.string.show_subTitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                updateUI(0);
                mCallbacks.onCrimeSelected(crime);
                return true;
            case R.id.show_subTitle:
                mSubTitleVisible = !mSubTitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubTitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubTitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subTitle = getResources().getQuantityString(R.plurals.subTitle_plural, crimeCount, crimeCount);

        if (!mSubTitleVisible){
            subTitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subTitle);
    }
}
