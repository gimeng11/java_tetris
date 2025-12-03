import java.io.IOException;

public class Main {

    static int col = 25;                  // 세로(높이)
    static int row = 10;                  // 가로(폭)
    private static int[][] Tetris = new int[col][row];  // Tetris[y][x]

    // 마지막 키 입력 저장용 (입력 스레드가 채우고, 메인 루프가 소비)
    static volatile int lastKey = -1;

    // ================== 기본 초기화 ==================
    static void init() {
        for (int y = 0; y < col; y++) {
            for (int x = 0; x < row; x++) {
                Tetris[y][x] = 0;
            }
        }
    }

    // ================== 화면 출력 ==================
    static void print_tetris() {
        for (int y = 0; y < col; y++) {
            for (int x = 0; x < row; x++) {
                if (Tetris[y][x] == 0)      System.out.print(" □ ");
                else if (Tetris[y][x] == 2) System.out.print(" ● ");
                else                        System.out.print(" ■ ");
            }
            System.out.println();
        }
    }

    // ================== 화면 지우기 ==================
    static void clear_screen() {
        // ANSI 시도 후, 안 먹으면 그냥 줄바꿈으로 밀기
        System.out.print("\u001b[H\u001b[2J");
        System.out.flush();
        System.out.println("\n".repeat(3));
    }

    // ================== 블록 생성 ==================
    static void make_blocks() {
        int r = (int)(Math.random() * 7) + 1; // 1~7 랜덤
        if (!random_blocks(r)) {
            game_over();
        }
    }

    static boolean random_blocks(int r) {
        // 생성 위치는 맨 위쪽 몇 줄 사용 (y=0~3)
        // 생성 전에 충돌 확인
        switch (r) {
            case 1 -> { // I 모양 ----
                for (int x = 3; x < 7; x++) {
                    if (Tetris[0][x] != 0) return false;
                }
                for (int x = 3; x < 7; x++) {
                    Tetris[0][x] = 2;
                }
            }
            case 2 -> { // └─ 모양
                if (Tetris[0][5] != 0 ||
                        Tetris[1][3] != 0 || Tetris[1][4] != 0 || Tetris[1][5] != 0) return false;

                Tetris[0][5] = 2;
                Tetris[1][3] = 2;
                Tetris[1][4] = 2;
                Tetris[1][5] = 2;
            }
            case 3 -> { // ─┘ 모양
                if (Tetris[0][3] != 0 ||
                        Tetris[1][3] != 0 || Tetris[1][4] != 0 || Tetris[1][5] != 0) return false;

                Tetris[0][3] = 2;
                Tetris[1][3] = 2;
                Tetris[1][4] = 2;
                Tetris[1][5] = 2;
            }
            case 4 -> { // T 모양
                if (Tetris[0][4] != 0 ||
                        Tetris[1][3] != 0 || Tetris[1][4] != 0 || Tetris[1][5] != 0) return false;

                Tetris[0][4] = 2;
                Tetris[1][3] = 2;
                Tetris[1][4] = 2;
                Tetris[1][5] = 2;
            }
            case 5 -> { // ㅁ 모양
                if (Tetris[0][4] != 0 || Tetris[0][5] != 0 ||
                        Tetris[1][4] != 0 || Tetris[1][5] != 0) return false;

                Tetris[0][4] = 2;
                Tetris[0][5] = 2;
                Tetris[1][4] = 2;
                Tetris[1][5] = 2;
            }
            case 6 -> { // S 모양
                if (Tetris[0][4] != 0 || Tetris[0][5] != 0 ||
                        Tetris[1][5] != 0 || Tetris[1][6] != 0) return false;

                Tetris[0][4] = 2;
                Tetris[0][5] = 2;
                Tetris[1][5] = 2;
                Tetris[1][6] = 2;
            }
            case 7 -> { // Z 모양
                if (Tetris[0][4] != 0 || Tetris[0][5] != 0 ||
                        Tetris[1][3] != 0 || Tetris[1][4] != 0) return false;

                Tetris[0][4] = 2;
                Tetris[0][5] = 2;
                Tetris[1][3] = 2;
                Tetris[1][4] = 2;
            }
        }
        return true;
    }

    // ================== 현재 조작 블록 -> 굳히기 ==================
    static void freeze_blocks() {
        for (int y = 0; y < col; y++) {
            for (int x = 0; x < row; x++) {
                if (Tetris[y][x] == 2) {
                    Tetris[y][x] = 1;
                }
            }
        }
    }

    // ================== 아래로 한 칸 자동 낙하 ==================
    static boolean can_fall() {
        for (int y = 0; y < col; y++) {
            for (int x = 0; x < row; x++) {
                if (Tetris[y][x] == 2) {
                    // 맨 아래면 더 못 감
                    if (y == col - 1) return false;
                    // 아래가 굳은 블록이면 못 감
                    if (Tetris[y + 1][x] == 1) return false;
                }
            }
        }
        return true;
    }

    static void fall_step() {
        if (can_fall()) {
            // 아래로 한 칸 이동 (아래에서 위로 스캔)
            for (int y = col - 2; y >= 0; y--) {
                for (int x = 0; x < row; x++) {
                    if (Tetris[y][x] == 2) {
                        Tetris[y][x] = 0;
                        Tetris[y + 1][x] = 2;
                    }
                }
            }
        } else {
            // 더 못 떨어지면 굳히고 새 블록 생성
            freeze_blocks();
            make_blocks();
        }
    }

    // ================== 좌/우/수동 아래 이동 ==================
    static void left() {
        // 이동 가능 여부 검사
        for (int y = 0; y < col; y++) {
            for (int x = 0; x < row; x++) {
                if (Tetris[y][x] == 2) {
                    if (x == 0) return;              // 왼쪽 벽
                    if (Tetris[y][x - 1] == 1) return; // 왼쪽에 굳은 블록
                }
            }
        }
        // 실제 이동
        for (int y = 0; y < col; y++) {
            for (int x = 1; x < row; x++) {
                if (Tetris[y][x] == 2) {
                    Tetris[y][x] = 0;
                    Tetris[y][x - 1] = 2;
                }
            }
        }
    }

    static void right() {
        // 이동 가능 여부 검사
        for (int y = 0; y < col; y++) {
            for (int x = 0; x < row; x++) {
                if (Tetris[y][x] == 2) {
                    if (x == row - 1) return;           // 오른쪽 벽
                    if (Tetris[y][x + 1] == 1) return;  // 오른쪽에 굳은 블록
                }
            }
        }
        // 실제 이동 (오른쪽은 역순 스캔)
        for (int y = 0; y < col; y++) {
            for (int x = row - 2; x >= 0; x--) {
                if (Tetris[y][x] == 2) {
                    Tetris[y][x] = 0;
                    Tetris[y][x + 1] = 2;
                }
            }
        }
    }

    static void down() {
        // 수동으로 한 칸 아래 (S키 같은 느낌)
        if (can_fall()) {
            for (int y = col - 2; y >= 0; y--) {
                for (int x = 0; x < row; x++) {
                    if (Tetris[y][x] == 2) {
                        Tetris[y][x] = 0;
                        Tetris[y + 1][x] = 2;
                    }
                }
            }
        } else {
            freeze_blocks();
            make_blocks();
        }
    }

    static void rotate() {
        // 회전은 나중에… (지금은 일단 패스)
    }

    // ================== 게임 오버 ==================
    static void game_over() {
        clear_screen();
        System.out.println("GAME OVER");
        System.exit(0);
    }

    // ================== 입력 처리 ==================
    static void startInputThread() {
        new Thread(() -> {
            try {
                while (true) {
                    int ch = System.in.read();
                    if (ch == -1) break;
                    if (ch == '\r' || ch == '\n') continue;  // 엔터는 무시
                    lastKey = ch;
                }
            } catch (IOException e) {
                // 그냥 무시
            }
        }).start();
    }

    static void handleInput() {
        int ch = lastKey;
        if (ch == -1) return;     // 새 입력 없음
        lastKey = -1;             // 한 번 소비하고 초기화

        char c = Character.toLowerCase((char) ch);
        switch (c) {
            case 'a' -> left();
            case 'd' -> right();
            case 's' -> down();
            case 'r' -> rotate();
        }
    }

    // ================== 메인 루프 ==================
    public static void main(String[] args) throws Exception {

        init();
        make_blocks();
        startInputThread();

        while (true) {
            clear_screen();
            print_tetris();

            // 입력 한 번 처리
            handleInput();

            // 자동으로 한 칸 떨어뜨리기
            fall_step();

            // 속도 조절
            Thread.sleep(700);
        }
    }
}
