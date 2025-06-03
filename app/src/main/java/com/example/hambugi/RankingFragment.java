package com.example.hambugi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.ArrayMap;
import android.view.*;
import android.widget.*;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class RankingFragment extends DialogFragment {
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        ListView listView_ranking = view.findViewById(R.id.listview_ranking);

        List<String> rankingList = new ArrayList<>();
        // DB에서 사용자의 닉네임, 점수를 가져와 리스트에 저장한다.

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                rankingList
        );

        listView_ranking.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        db.collection("rankings")
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    rankingList.clear();
                    int rank = 1;
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String nickname = doc.getString("nickname");
                        Long score = doc.getLong("score");
                        if (nickname != null && score != null) {
                            rankingList.add(rank + "위: " + nickname + " - " + score + "점");
                            rank++;
                        }
                    }
                    adapter.notifyDataSetChanged();  // ListView 갱신
                })
                .addOnFailureListener(e -> {
                    rankingList.clear();
                    rankingList.add("랭킹 정보를 불러올 수 없습니다.");
                    adapter.notifyDataSetChanged();
                });
        // 닫기 버튼
        ImageButton btn_exit = view.findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(v -> dismiss());

        return view;
    }
}