package com.example.hambugi;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class fragment_menu extends DialogFragment {

    public fragment_menu() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 메뉴가 열릴 때 두 가지 모두 일시정지
        GameManager.getInstance().pauseGame();
        GameManager.getInstance().pausePatience();

        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Resume 버튼 클릭: 게임 시간 + 인내심 재개
        view.findViewById(R.id.btn_resume).setOnClickListener(v -> {
            GameManager.getInstance().resumeGame();
            GameManager.getInstance().resumePatience();
            dismiss();
        });

        // Exit 버튼 클릭: 메인화면으로 복귀
        view.findViewById(R.id.btn_exit).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 혹시 모를 예외 상황에서도 resume 처리 (안전장치)
        GameManager.getInstance().resumeGame();
        GameManager.getInstance().resumePatience();
    }
}
