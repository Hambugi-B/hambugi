package com.example.hambugi;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

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

    // 손님 & 주문 관리
    private List<String> currentOrder;
    private final List<String> unlockedIngredients = new ArrayList<>(
            Arrays.asList("bun_bottom", "bun_top", "patty", "lettuce")
    );
    private int currentCustomerImageId = R.drawable.customer;

    // 스테이지 및 점수 관리
    private int currentStage = 1;
    private int score = 0;
    private boolean isStageLoaded = false;

    // 골드 관리 (DB 포함)
    private int gold = 0;
    private final FirebaseFirestore db;
    private final String userId;
    private boolean isGoldLoaded = false;

    // 아티팩트 관리
    private final Map<String, Artifact> ownedArtifacts = new HashMap<>();

    private void initArtifacts() {
        ownedArtifacts.put("flowerpot", new Artifact("flowerpot", "화분", 150, "보너스 점수 +5%", ArtifactEffect.SCORE_BONUS, 0.05));
        ownedArtifacts.put("clock", new Artifact("clock", "시계", 200, "인내심 +5%", ArtifactEffect.PATIENCE_BOOST, 0.05));
        ownedArtifacts.put("curtain", new Artifact("curtain", "커튼", 250, "인내심 +10%", ArtifactEffect.PATIENCE_BOOST, 0.1));
    }
    private GameManager() {
        generateNewOrder();

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userId = user.getUid();
            loadGameDataAndNotify(() -> {
                // 여기선 초기화 중이니까 특별히 UI 갱신은 필요없음
            });
        } else {
            throw new IllegalStateException("로그인 유저 정보가 없습니다. 인증을 확인하세요.");
        }

        initArtifacts();
    }

    public static GameManager getInstance() {
        if (instance == null) instance = new GameManager();
        return instance;
    }

    // -------- 시간 관리 --------
    public long getFullMillis() {
        return FULL_MILLIS;
    }

    public long getRemainingMillis() {
        return remainingMillis;
    }

    public void setRemainingMillis(long ms) {
        remainingMillis = ms;
    }

    public void decreaseRemainingMillis(long ms) {
        remainingMillis = Math.max(0, remainingMillis - ms);
    }

    public void pauseGame() {
        isPaused = true;
    }

    public void resumeGame() {
        isPaused = false;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public int getCurrentHour() {
        long elapsedMillis = getFullMillis() - getRemainingMillis();
        int elapsedSec = (int) (elapsedMillis / 1000);
        int totalGameMinutes = (int) ((elapsedSec / 300.0) * (14 * 60));

        return 8 + totalGameMinutes / 60;
    }

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
        double limit = Math.max(5000, 10000 - (currentStage - 1) * 1000);

        if (isPeakTime()) {
            limit *= 0.8; // 피크타임 시 인내심 20% 감소
        }

        limit = limit * getPatienceBonus();   // 아티팩트 보정
        return (long) limit;
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

    // -------- 손님 & 주문 관리 --------
    public void generateNewOrder() {
        List<String> unlockedToppings = new ArrayList<>(unlockedIngredients);
        unlockedToppings.remove("bun_bottom");
        unlockedToppings.remove("bun_top");

        int baseMiddle = 1 + currentStage / 2; // 스테이지가 올라갈수록 조금씩 증가
        int maxMiddle = Math.min(6, baseMiddle); // 최대값 6개 제한

        if (isPeakTime()) {
            maxMiddle++;
        }  // 피크타임이면 재료 1개 추가

        int count = new Random().nextInt(maxMiddle) + 1;

        List<String> middleSelected = new ArrayList<>();
        middleSelected.add("patty");    // 패티 1장은 무조건 포함
        count--;

        // 스테이지에 따른 추가 패티 확률 함수
        int extraPatty = getExtraPattyCount(currentStage);
        for (int i = 0; i < extraPatty; i++) {
            middleSelected.add("patty");
        }

        for (int i = 0; i < count - extraPatty; i++) {
            String item = unlockedToppings.get(new Random().nextInt(unlockedToppings.size()));
            middleSelected.add(item);
        }

        // 뽑힌 중간 재료 섞기
        Collections.shuffle(middleSelected);

        currentOrder = new ArrayList<>();
        currentOrder.add("bun_bottom");
        currentOrder.addAll(middleSelected);
        currentOrder.add("bun_top");

        resetCustomerTimer();
    }
    private int getExtraPattyCount(int stage) {
        Random rand = new Random();
        int extra = 0;

        // 스테이지별 추가 패티 확률
        int chance = Math.min(60, 5 + stage * 5);  // 스테이지 10일떄 확률 80%

        if (rand.nextInt(100) < chance) extra++; // 1장 추가
        if (rand.nextInt(100) < chance / 2) extra++; // 50프로의 확률로 한번더

        return extra;
    }
    public List<String> getCurrentOrder() {
        return currentOrder;
    }
    public void serveCustomer() {
        generateNewOrder();
    }
    public void unlockIngredient(String ingredient) {
        if (!unlockedIngredients.contains(ingredient)) unlockedIngredients.add(ingredient);
    }
    public List<String> getUnlockedIngredients() {
        return unlockedIngredients;
    }
    public int getCurrentCustomerImageId() {
        return currentCustomerImageId;
    }
    public void setCurrentCustomerImageId(int id){
        currentCustomerImageId = id;
    }

    // -------- 스테이지 관리 --------
    public int getCurrentStage() {
        return currentStage;
    }
    public void nextStage() {
        currentStage++;
        resetGameTime();

        // 해당 스테이지 종료 후 재료 자동 해금
        if (currentStage > 3) unlockIngredient("cheese");
        if (currentStage > 6) unlockIngredient("tomato");

        patienceLimit = getCurrentPatienceLimit();

        saveGameDataToFirestore();
    }
    public void resetStage() {
        currentStage = 1;
        resetGameTime();
    }
    public void plusStage() {
        currentStage++;
    }
    public boolean isStageInitialized() {
        return isStageLoaded;
    }
    public void resetGameTime() {
        remainingMillis = FULL_MILLIS;
    }

    // -------- 피크 타임 관리 --------
    public boolean isPeakTime() {
        int hour = getCurrentHour();
        return (hour >= 12 && hour <= 14) || (hour >= 18 && hour <= 20);
    }

    // -------- 점수 관리 ---------
    public void resetScore() {
        score = 0;
    }
    public void addScore() {
        int baseScore = 100;    // 기본 점수 100점
        int stageBonus = (currentStage - 1) * 10;  // 스테이지 보너스 점수

        double patienceRatio = (double) getRemainingPatience() / getCurrentPatienceLimit();
        double speedBonus = 1.0 + patienceRatio; // 남은 인내심별 보너스(최대 2배)

        double earnedScore = (baseScore + stageBonus) * speedBonus;
        if (isPeakTime()) {   // 피크타임이면 점수 1.2배
            earnedScore *= 1.2;
        }

        earnedScore *= getScoreBonus();   // 아티팩트 보정
        score += (int)earnedScore;
    }
    public int getScore() {
        return score;
    }

    // -------- 버거 가격 계산 --------
    public int calculateBurgerPrice(List<String> burger) {
        int total = 0;
        int price = 0;
        for (String ingredient : burger) {
            switch (ingredient) {
                case "patty":
                    price = 5;
                    break;
                case "cheese":
                    price = 3;
                    break;
                case "tomato":
                    price = 4;
                    break;
                default:
                    price = 2;
                    break;
            }
            total += price;
        }

        if (isPeakTime()) {   // 피크타임이면 버거 가격 1.2배
            total = (int) Math.round(total * 1.2);
        }

        return total;
    }

    // -------- 골드 관리 --------
    public void resetGold() {
        gold = 0;
    }
    public void setGold(int amount) {
        this.gold = amount;
    }
    public int getGold() {
        return gold;
    }
    public void addGold(int amount) {
        setGold(this.gold + amount);
    }
    public boolean isGoldInitialized() {
        return isGoldLoaded;
    }

    // ---------- 아티팩트 관리 ---------
    public PurchaseResult purchaseArtifact(String id) {
        Artifact artifact = ownedArtifacts.get(id);
        if (artifact == null) return PurchaseResult.ALREADY_PURCHASED;
        if (artifact.isPurchased()) return PurchaseResult.ALREADY_PURCHASED;
        if (gold < artifact.getPrice()) return PurchaseResult.NOT_ENOUGH_GOLD;

        setGold(this.gold - artifact.getPrice());
        artifact.setPurchased(true);
        saveGameDataToFirestore();

        return PurchaseResult.SUCCESS;
    }
    public boolean isPurchased(String id) {
        Artifact artifact = ownedArtifacts.get(id);
        return artifact != null && artifact.isPurchased();
    }
    public List<Artifact> getPurchasedArtifacts() {
        List<Artifact> purchasedList = new ArrayList<>();
        for (Artifact artifact : ownedArtifacts.values()) {
            if (artifact.isPurchased()) {
                purchasedList.add(artifact);
            }
        }
        return purchasedList;
    }
    public double getPatienceBonus() {    // 인내심 보정
        double bonus = 1.0;
        for (Artifact artifact : ownedArtifacts.values()) {
            if (artifact.isPurchased() && artifact.getEffect() == ArtifactEffect.PATIENCE_BOOST) {
                bonus += artifact.getValue();
            }
        }
        return bonus;
    }
    public double getScoreBonus() {   // 점수 보정
        double bonus = 1.0;
        for (Artifact artifact : ownedArtifacts.values()) {
            if (artifact.isPurchased() && artifact.getEffect() == ArtifactEffect.SCORE_BONUS) {
                bonus += artifact.getValue();
            }
        }
        return bonus;
    }

    // ----------- DB 저장 관리 --------
    private void saveGameDataToFirestore() {
        DocumentReference docRef = db.collection("users").document(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("gold", gold);
        data.put("score", score);
        data.put("stage", currentStage);

        docRef.set(data, SetOptions.merge())
                .addOnFailureListener(e -> {
                    // 실패 시 로그 처리
                    Log.e("GameManager", "Firestore 저장 실패: " + e.getMessage(), e);
                });
    }
    public void loadGameDataAndNotify(Runnable onComplete) {
        db.collection("users").document(userId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                Long g = doc.getLong("gold");
                Long s = doc.getLong("score");
                Long st = doc.getLong("stage");

                if (g != null) this.gold = g.intValue();
                if (s != null) this.score = s.intValue();
                if (st != null) this.currentStage = st.intValue();
            }
            isGoldLoaded = true;
            isStageLoaded = true;
            if (onComplete != null) onComplete.run();
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "게임 데이터 불러오기 실패", e);
            if (onComplete != null) onComplete.run();  // 실패해도 후속 처리 필요 시
        });
    }

    public static class UserData {
        public int gold;
        public int score;
        public int stage;

        public UserData() {
        }
        public UserData(int gold, int score, int stage) {
            this.gold = gold;
            this.score = score;
            this.stage = stage;
        }
    }
}
