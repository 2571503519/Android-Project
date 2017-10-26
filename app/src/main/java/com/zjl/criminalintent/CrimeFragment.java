package com.zjl.criminalintent;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.zjl.criminalintent.domain.Crime;
import com.zjl.criminalintent.domain.CrimeLab;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by lenovo on 2017/8/8.
 */
public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment";
    private static final int REQUEST_CODE = 0;
    private static final int REQUEST_CODE_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;
    private static final int REQUEST_PHOTO_VIEW = 4;
    private int mPhotoWidth;
    private int mPhotoHeight;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private CheckBox mSolvedCheckBox;
    private ImageView mPhotoView;
    private ImageButton mPhotoButton;
    private File mPhotoFile;
    private Callbacks mCallbacks;
//    private int mPosition;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_PHOTO = "DialogPhoto";

    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
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
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

   /* public void returnResult(){
        Intent data = new Intent();
        data.putExtra(CrimeListFragment.POSITION, mPosition);
        getActivity().setResult(Activity.RESULT_OK, data);
    }*/

   private void updateCrime(){
       CrimeLab.get(getActivity()).updateCrime(mCrime);
       mCallbacks.onCrimeUpdated(mCrime);
   }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return ;
        }
        if(requestCode == REQUEST_CODE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        }
        if (requestCode == REQUEST_CODE_TIME){
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        }
        if (requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

            try{
                if(cursor.getCount() == 0){
                    return ;
                }
                cursor.moveToFirst();
                String suspect = cursor.getString(0);
                mCrime.setSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);
                mCallButton.setEnabled(true);
            }finally {
                cursor.close();
            }
        }
        if (requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.zjl.criminalintent.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateCrime();
            updatePhotoView();
        }
        if (requestCode == REQUEST_PHOTO_VIEW){
            if(data.getStringExtra(PhotoViewFragment.EXTRA_DELETE_PHOTO) == "deletePhoto"){
                if (mPhotoFile.exists()) {
                    mPhotoFile.delete();
                }
                updatePhotoView();
            }
        }
    }

    private void updateDate() {
        mDateButton.setText(DateFormat.format("MMM dd, yyyy", mCrime.getDate()));
        mTimeButton.setText(DateFormat.format("hh:mm", mCrime.getDate()));
    }

    private void updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = null;
            if (mPhotoWidth == 0 || mPhotoHeight == 0){
                bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            }else{
                bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), mPhotoWidth, mPhotoHeight);
            }
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private String getCrimeReport(){
        String solvedString = null;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }else{
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM, dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        }else{
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = (EditText) v.findViewById(R.id.et_crime_title_hint);
        mDateButton = (Button) v.findViewById(R.id.btn_crime_date);
        mTimeButton = (Button) v.findViewById(R.id.btn_crime_time);
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.cb_crime_solved);
        mReportButton = (Button) v.findViewById(R.id.btn_crime_report);
        mSuspectButton = (Button) v.findViewById(R.id.btn_crime_suspect);
        mCallButton = (Button) v.findViewById(R.id.btn_crime_call);
        mPhotoButton = (ImageButton) v.findViewById(R.id.ibtn_crime_camera);
        mPhotoView = (ImageView) v.findViewById(R.id.iv_crime_photo);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mTitleField.setText(mCrime.getTitle());
        updateDate();
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);*/

//                IntentBuilder是一个用来构造ACTION_SEND和ACTION_SEND_MULTIPLE操作接口意图的辅助类，
//                  可实现intent共享，并使启动的activity实现内容共享，包括调用activity的Component名和包名。

                ShareCompat.IntentBuilder sc = ShareCompat.IntentBuilder.from(getActivity());
                sc.setType("text/plain");
                sc.setText(getCrimeReport());
                sc.setSubject(getString(R.string.crime_report_subject));
                sc.createChooserIntent();
                sc.startChooser();

            }
        });

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_CODE);
                dialog.show(fragmentManager, DIALOG_DATE);
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_CODE_TIME);
                dialog.show(fragmentManager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }

        if(mCrime.getSuspect() == null){
            mCallButton.setEnabled(false);
        }
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = getPhoneNumber(getActivity());
                Intent intent = new Intent();
                Uri number = Uri.parse("tel:" + phoneNumber);
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(number);
                startActivity(intent);
            }
        });

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.zjl.criminalintent.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(captureImage, REQUEST_PHOTO);
                }
            }
        });

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoFile == null || !mPhotoFile.exists()){
                    return;
                } else {
                    FragmentManager fragmentManager = getFragmentManager();
                    PhotoViewFragment dialog = PhotoViewFragment.newInstance(mPhotoFile.getPath());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_PHOTO_VIEW);
                    dialog.show(fragmentManager, DIALOG_PHOTO);
                }
            }
        });
        final ViewTreeObserver viewTreeObserver = mPhotoView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPhotoWidth = mPhotoView.getWidth();
                mPhotoHeight = mPhotoView.getHeight();
                updatePhotoView();
                mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        return v;
    }

    private String getPhoneNumber(Context context){
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " = ? ",
                new String[]{mCrime.getSuspect()},
                null);
        cursor.moveToFirst();
        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        Cursor cursor1 = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{contactId},
                null
        );
        cursor1.moveToFirst();
        String phoneNum = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        cursor.close();
        cursor1.close();
        return phoneNum;
    }

    /*public void number(Context context, String name) {
        //使用ContentResolver查找联系人数据
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        //遍历查询结果，找到所需号码
        while (cursor.moveToNext()) {
            //获取联系人ID
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //获取联系人的名字
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            if (name.equals(contactName)) {
                //使用ContentResolver查找联系人的电话号码
                Cursor phone = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                if (phone.moveToNext()) {
                    String phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.d(TAG, "电话：" + phoneNumber);
                    break;
                }

            }
        }
    }*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).removeCrime(mCrime);
                if (getActivity().findViewById(R.id.detail_fragment_container) != null){
                    updateCrime();
                    return true;
                } else {
                    getActivity().finish();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
