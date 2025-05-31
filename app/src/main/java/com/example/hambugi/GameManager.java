package com.example.hambugi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameManager {

    private static GameManager instance;

    private List<String> currentOrder;          // 현재 손님의 주문
    private long customerStartTime;             // 손님 등장 시각(ms)
    private final long patienceLimit = 10000;   // 인내심 기본 10초
    private int currentStage = 1;               // 스테이지

    /* ───────── 일시정지 & 타이머 이어붙이기 ───────── */
    private boolean isPaused = false;
    private long   remainingMillis = -1;              // ★ 남은 타이머 ms (-1이면 미초기화)

    private int gold = 0;

    private final FirebaseFirestore db;
    private final String userId;

    /* 해금된 재료 */
    private final List<String> unlockedIngredients =
            new ArrayList<>(Arrays.asList("bun_bottom", "bun_top", "patty", "lettuce"));

    /* ─────────────────────────────────────────────── */

    private GameManager() {
        generateNewOrder();

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userId = user.getUid();
            loadGoldFromFirestore();
        } else {
            userId = "defaultUser";
        }
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    /* ───────── 골드 저장 / 불러오기 ───────── */

    public void setGold(int amount) {
        this.gold = amount;
        saveGoldToFirestore();
    }

    public int getGold() { return gold; }

    public void addGold(int amount)      { setGold(this.gold + amount); }
    public void subtractGold(int amount) { setGold(Math.max(0, this.gold - amount)); }

    private void saveGoldToFirestore() {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.update("gold", gold).addOnFailureListener(e -> docRef.set(new UserData(gold)));
    }

    private void loadGoldFromFirestore() {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Long g = doc.getLong("gold");
                        if (g != null) this.gold = g.intValue();
                    }
                });
    }

    public static class UserData {
        public int gold;
        public UserData() {}
        public UserData(int gold) { this.gold = gold; }
    }

    /* ───────── 일시정지 제어 ───────── */

    public void pause()  { isPaused = true; }
    public void resume() { isPaused = false; }
    public boolean isPaused() { return isPaused; }

    // ★ 프래그먼트에서 사용할 방식(setter/getter)
    public void setPaused(boolean p) { isPaused = p; }          // 호환용
    // ★ 남은 타이머 ms 저장 / 조회
    public void setRemainingMillis(long ms) { remainingMillis = ms; }
    public long  getRemainingMillis()       { return remainingMillis; }

    /* ───────── 게임 로직 ───────── */

    public void setStage(int stage) { this.currentStage = stage; }

    public List<String> getCurrentOrder() { return currentOrder; }

    public boolean isCustomerExpired() {
        return System.currentTimeMillis() - customerStartTime > getCurrentPatienceLimit();
    }

    public long getRemainingTime() {
        long elapsed = System.currentTimeMillis() - customerStartTime;
        return Math.max(0, getCurrentPatienceLimit() - elapsed);
    }

    public long getCurrentPatienceLimit() {
        return Math.max(5000, 10000 - (currentStage - 1) * 500);
    }

    public void generateNewOrder() {
        List<String> unlockedToppings = new ArrayList<>(unlockedIngredients);
        unlockedToppings.remove("bun_bottom");
        unlockedToppings.remove("bun_top");

        int maxMiddle = Math.min(4, 1 + currentStage);
        int count = new Random().nextInt(maxMiddle) + 1;

        List<String> middleSelected = new ArrayList<>();

        if (unlockedToppings.size() >= count) {
            Collections.shuffle(unlockedToppings);
            middleSelected.addAll(unlockedToppings.subList(0, count));
        } else {
            for (int i = 0; i < count; i++) {
                String item = unlockedToppings.get(new Random().nextInt(unlockedToppings.size()));
                middleSelected.add(item);
            }
        }

        currentOrder = new ArrayList<>();
        currentOrder.add("bun_bottom");
        currentOrder.addAll(middleSelected);
        currentOrder.add("bun_top");

        customerStartTime = System.currentTimeMillis();
    }

    public void serveCustomer() { generateNewOrder(); }

    public void unlockIngredient(String ingredient) {
        if (!unlockedIngredients.contains(ingredient)) unlockedIngredients.add(ingredient);
    }

    public List<String> getUnlockedIngredients() { return unlockedIngredients; }
}
