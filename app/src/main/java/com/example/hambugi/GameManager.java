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

    private GameManager() {
        generateNewOrder(); // 초기 손님 생성
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    // 새로운 손님 주문 생성
    public void generateNewOrder() {
        String[] toppings = {"patty", "cheese", "lettuce", "tomato"};
        List<String> middle = Arrays.asList(toppings);
        Collections.shuffle(middle);

        int count = new Random().nextInt(3) + 1; // 중간 재료 개수 1~3개
        List<String> middleSelected = middle.subList(0, count);

        currentOrder = new ArrayList<>();
        currentOrder.add("bun_bottom"); // 시작은 항상 아래 빵
        currentOrder.addAll(middleSelected); // 랜덤 중간 재료
        currentOrder.add("bun_top"); // 끝은 항상 위 빵

        customerStartTime = System.currentTimeMillis(); // 손님 등장 시간 기록
    }

    // 현재 손님의 주문을 반환
    public List<String> getCurrentOrder() {
        return currentOrder;
    }

    // 손님의 인내심이 만료되었는지 확인
    public boolean isCustomerExpired() {
        return System.currentTimeMillis() - customerStartTime > patienceLimit;
    }

    // 남은 인내심 시간(ms) 반환
    public long getRemainingTime() {
        long elapsed = System.currentTimeMillis() - customerStartTime;
        return Math.max(0, patienceLimit - elapsed);
    }

    // 햄버거 제공 후 새로운 손님으로 교체
    public void serveCustomer() {
        generateNewOrder();
    }
}