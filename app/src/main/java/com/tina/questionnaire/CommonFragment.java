package com.tina.questionnaire;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tina.questionnaire.entity.QuestionsObject;


/**
 * Created by Tina
 * on 2017/10/8
 * description: Generate fragments according to different question type
 */

public class CommonFragment extends Fragment {

    View mRootView;
    protected Context mActivity;
    private QuestionsObject mObject;
    private int questionsNum;
    private QuestionViewCreator mQuestionViewCreator;
    private ViewGroup mContent;
    private Button mBtnPrev;
    private OnPageChangeListener mListener;
    private Button mBtnNext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mObject = (QuestionsObject) getArguments().getSerializable(QuestionnaireNewActivity.QUESTION_OBJECT);
        questionsNum = getArguments().getInt(QuestionnaireNewActivity.QUESTION_SIZE);
        /**
         * generate different type of viewCreators according to question type
         */
        if (mObject.type.equals(QuestionsObject.SINGLE_CHOICE)) {
            mQuestionViewCreator = new SingleChoiceViewCreator();
        } else if (mObject.type.equals(QuestionsObject.MULTI_CHOICE)) {
            mQuestionViewCreator = new MultiChoiceViewCreator();
        } else if (mObject.type.equals(QuestionsObject.TEXT_INPUT)) {
            mQuestionViewCreator = new TextInputViewCreator();
        } else if(mObject.type.equals(QuestionsObject.DATE_PICKER)){
            mQuestionViewCreator = new DatePickViewCreator();
        } else{
            Toast.makeText(mActivity,"Json file contains error!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(mActivity, MainActivity.class));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_common, null, false);
        mContent = (ViewGroup) findViewById(R.id.ll_content);
        if (mActivity instanceof QuestionnaireNewActivity) {
            mListener = (OnPageChangeListener) mActivity;
        }
        /**
         * obtain view created in the QuestionViewCreator and add to the current ViewGroup
         */
        mContent.addView(mQuestionViewCreator.getView(mActivity, mObject));
        /**
         * add prev question button and next question button
         */
        mBtnPrev = (Button) findViewById(R.id.btn_prev);
        mBtnNext = (Button)findViewById(R.id.btn_next);
        mBtnPrev.setVisibility(mObject.index == 0 ? View.GONE :View.VISIBLE);
        mBtnNext.setText(mObject.index == (questionsNum - 1) ? "SUMBMIT" : "NEXT");
        mBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 *  callback onPrev() method in questionnaire activity
                 */
                mListener.onPrev(mObject);
            }
        });
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuestionViewCreator.check()) {
                    /**
                     *  if question is correctly answered, callback onPrev() method in questionnaire activity
                     */
                    mListener.onNext(mObject);
                }
            }
        });
        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = context;
    }


    @Override
    public void startActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        super.startActivity(intent);
    }


    protected View findViewById(int id) {
        return mRootView.findViewById(id);
    }

}
