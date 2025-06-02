package com.example.hambugi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.ArrayMap;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class RankingFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        ListView listView_ranking = view.findViewById(R.id.listview_ranking);

        List<String> rankingList = new ArrayList<>();
        // DB에서 사용자의 닉네임, 점수를 가져와 리스트에 저장한다.
        rankingList.add("1위: a - 1000점");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                rankingList
        );

        listView_ranking.setAdapter(adapter);

        // 닫기 버튼
        ImageButton btn_exit = view.findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(v -> dismiss());

        return view;
    }
}