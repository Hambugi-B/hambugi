package com.example.hambugi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameManager {
    private static GameManager instance;

    private List<String> currentOrder; // 현재 손님의 주문
    private long customerStartTime; // 현재 손님이 등장한 시간 (ms)
    private final long patienceLimit = 10000; // 손님의 인내심 지속 시간: 10초
    private int currentStage = 1; // 스테이지 단계

    // 해금된 재료 목록
    private List<String> unlockedIngredients = new ArrayList<>(Arrays.asList("bun_bottom", "bun_top", "patty", "lettuce"));

    private GameManager() {
        generateNewOrder(); // 초기 손님 생성
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }
    //메뉴 뜨는 동안 시간 멈추기
    private boolean isPaused = false;

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public boolean isPaused() {
        return isPaused;
    }
    // 스테이지 설정
    public void setStage(int stage) {
        this.currentStage = stage;
    }

    // 현재 손님의 주문을 반환
    public List<String> getCurrentOrder() {
        return currentOrder;
    }

    // 손님의 인내심이 만료되었는지 확인 (스테이지가 높을 수록 인내심 감소 최저 5000ms보장)
    public boolean isCustomerExpired() {
        return System.currentTimeMillis() - customerStartTime > getCurrentPatienceLimit();
    }

    // 남은 인내심 시간(ms) 반환
    public long getRemainingTime() {
        long elapsed = System.currentTimeMillis() - customerStartTime;
        return Math.max(0, getCurrentPatienceLimit() - elapsed);
    }

    // 인내심 max치 계산
    public long getCurrentPatienceLimit(){
        return Math.max(5000, 10000-(currentStage - 1) * 500); // 스테이지 당 0.5초씩 감소 최소 5초 보장
    }

    // 새로운 손님 주문 생성
    public void generateNewOrder() {
        // 해금된 재료 중 빵 제외
        List<String> unlockedToppings = new ArrayList<>(unlockedIngredients);
        unlockedToppings.remove("bun_bottom");
        unlockedToppings.remove("bun_top");

        int maxMiddle = Math.min(4, 1 + currentStage); // 스테이지 1→2개, 2→3개 ...
        int count = new Random().nextInt(maxMiddle) + 1; // 1 ~ maxMiddle개

        List<String> middleSelected = new ArrayList<>();

        if (unlockedToppings.size() >= count){
            // 중복 없이 재료 선택
            Collections.shuffle(unlockedToppings);
            middleSelected.addAll(unlockedToppings.subList(0,count));
        } else {
            // 중복 허용으로 재료 선택
            for (int i = 0; i < count; i++){
                String item = unlockedToppings.get(new Random().nextInt(unlockedToppings.size()));
                middleSelected.add(item);
            }
        }

        currentOrder = new ArrayList<>();
        currentOrder.add("bun_bottom"); // 시작은 항상 아래 빵
        currentOrder.addAll(middleSelected); // 랜덤 중간 재료
        currentOrder.add("bun_top"); // 끝은 항상 위 빵

        customerStartTime = System.currentTimeMillis(); // 손님 등장 시간 기록
    }

    // 햄버거 제공 후 새로운 손님으로 교체
    public void serveCustomer() {
        generateNewOrder();
    }

    // 재료 해금 함수
    public void unlockIngredient(String ingredient) {
        if (!unlockedIngredients.contains(ingredient)) {
            unlockedIngredients.add(ingredient);
        }
    }

    // 현재 해금된 재료 리스트 조회
    public List<String> getUnlockedIngredients() {
        return unlockedIngredients;
    }




}