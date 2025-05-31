package com.example.hambugi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class GameManager {

    private static GameManager instance;

    // 시간 관리
    private final long FULL_MILLIS = 5 * 60 * 1000;  // 5분이 하루
    private long remainingMillis = FULL_MILLIS;
    private boolean isPaused = false;

    // 인내심 관리
    private long customerStartTime;
    private long patienceLimit = 10000;
    private boolean isPatiencePaused = false;
    private long remainingPatienceWhenPaused = -1;

    // 주문 관리
    private List<String> currentOrder;
    private final List<String> unlockedIngredients = new ArrayList<>(
            Arrays.asList("bun_bottom", "bun_top", "patty", "lettuce")
    );
    private int currentStage = 1;

    // 골드 관리 (DB 포함)
    private int gold = 0;
    private final FirebaseFirestore db;
    private final String userId;
    private boolean isGoldLoaded = false;

    private GameManager() {
        generateNewOrder();

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userId = user.getUid();
            loadGoldAndNotify(() -> {
                // 여기선 초기화 중이니까 특별히 UI 갱신은 필요없음
            });
        } else {
            userId = "defaultUser";
        }
    }

    public static GameManager getInstance() {
        if (instance == null) instance = new GameManager();
        return instance;
    }

    // -------- 시간 관리 --------
    public long getFullMillis() { return FULL_MILLIS; }
    public long getRemainingMillis() { return remainingMillis; }
    public void setRemainingMillis(long ms) { remainingMillis = ms; }
    public void decreaseRemainingMillis(long ms) {
        remainingMillis = Math.max(0, remainingMillis - ms);
    }
    public void pauseGame() { isPaused = true; }
    public void resumeGame() { isPaused = false; }
    public boolean isPaused() { return isPaused; }

    // -------- 인내심 관리 --------

    public void pausePatience() {
        if (!isPatiencePaused) {
            remainingPatienceWhenPaused = getRemainingPatience();
            isPatiencePaused = true;
        }
    }
    public void resumePatience() {
        if (isPatiencePaused) {
            customerStartTime = System.currentTimeMillis() - (patienceLimit - remainingPatienceWhenPaused);
            remainingPatienceWhenPaused = -1;
            isPatiencePaused = false;
        }
    }
    public boolean isPatiencePaused() {
        return isPatiencePaused;
    }
    public long getCurrentPatienceLimit() {
        return Math.max(5000, 10000 - (currentStage - 1) * 500);
    }

    public void resetCustomerTimer() {
        customerStartTime = System.currentTimeMillis();
        patienceLimit = getCurrentPatienceLimit();
    }

    public long getRemainingPatience() {
        long elapsed = System.currentTimeMillis() - customerStartTime;
        return Math.max(0, patienceLimit - elapsed);
    }

    public boolean isCustomerExpired() {
        return getRemainingPatience() <= 0;
    }

    // -------- 주문 관리 --------
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

        resetCustomerTimer();
    }

    public List<String> getCurrentOrder() { return currentOrder; }
    public void serveCustomer() { generateNewOrder(); }
    public void unlockIngredient(String ingredient) {
        if (!unlockedIngredients.contains(ingredient)) unlockedIngredients.add(ingredient);
    }
    public List<String> getUnlockedIngredients() { return unlockedIngredients; }

    // -------- 버거 가격 계산 --------
    public int calculateBurgerPrice(List<String> burger) {
        int total = 0;
        int price = 0;
        for (String ingredient : burger) {
            switch (ingredient) {
                case "cheese": price =  3; break;
                case "tomato": price =  4; break;
                default: price =  2; break;
            };
            total += price;
        }
        return total;
    }

    // -------- 골드 관리 --------
    public void setGold(int amount) {
        this.gold = amount;
        saveGoldToFirestore();
    }
    public int getGold() { return gold; }
    public void addGold(int amount) { setGold(this.gold + amount); }

    private void saveGoldToFirestore() {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.update("gold", gold).addOnFailureListener(e -> docRef.set(new UserData(gold)));
    }
    public void loadGoldAndNotify(Runnable onComplete) {
        db.collection("users").document(userId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                Long g = doc.getLong("gold");
                if (g != null) this.gold = g.intValue();
            }
            isGoldLoaded = true;
            onComplete.run();
        });
    }
    public boolean isGoldInitialized() {
        return isGoldLoaded;
    }

    public static class UserData {
        public int gold;
        public UserData() {}
        public UserData(int gold) { this.gold = gold; }
    }
}
