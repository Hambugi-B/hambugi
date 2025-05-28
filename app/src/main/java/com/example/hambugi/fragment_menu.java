package com.example.hambugi;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link fragment_menu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_menu extends DialogFragment { // 손님보다 위에 보이게 수정

    // 파라미터 정의
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public fragment_menu() {
        // 기본 생성자
    }

    // newInstance 팩토리 메서드
    public static fragment_menu newInstance(String param1, String param2) {
        fragment_menu fragment = new fragment_menu();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * 메뉴 프래그먼트 레이아웃을 inflate하고 버튼 동작을 설정
     * 게임 일시정지/재개 기능 포함
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GameManager.getInstance().pause(); // 메뉴 뜰 때 게임 정지

        // fragment_menu 레이아웃 inflate
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Resume 버튼 클릭 시 → 게임 재개 + 팝업 닫기
        view.findViewById(R.id.btn_resume).setOnClickListener(v -> {
            GameManager.getInstance().resume(); // 메뉴 닫으면 게임 재개
            dismiss(); // DialogFragment 닫기
        });

        // Exit 버튼 클릭 시 → MainActivity로 이동
        view.findViewById(R.id.btn_exit).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    /**
     * DialogFragment를 전체화면처럼 보이게 설정
     */
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    /**
     * 메뉴가 닫힐 때에도 반드시 게임 재개되도록 보장
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        GameManager.getInstance().resume(); // 안전하게 resume 호출
    }
}
