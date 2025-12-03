import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    static int col = 25; //테트리스 게임 높이 기본 20 + 블록 생성 공간 4 + 맨 아래 바닥 1
    static int row = 10; //테트리스 게임 폭 벽 없이 그냥 10
    private static int[][] Tetris = new int[col][row]; //테트리스 게임 공간 2차원 배열

    static void init() { //초기 게임 설정하는 함수
        for (int i = 0; i < col; i++) {
            for (int j = 0; j < row; j++) {
                Tetris[i][j] = 0; //모든 공간을 0으로 채움, 0은 게임 시스템 상 비어있는 공간
            }
        }
    }

    static void print_tetris() { //화면을 출력하는 함수

        for (int i = 0; i < col; i++) {
            for (int j = 0; j < row; j++) { //배열에서 값을 받아서 해당하는 것을 출력하는 함수, 0은 공백이고 "□"으로 표현, 2는 현재 조작중인 블록 "●"으로 표현
                if (Tetris[i][j] == 0)
                    System.out.print(" . ");
                else if(Tetris[i][j] == 2)
                    System.out.print(" + ");
                else
                    System.out.print(" = ");
            }
            System.out.println();
        }
    }

    static void make_blocks() { //맨 위에 블록을 생성하는 함수

            for (int j = 0; j < 10; j++) { //우선 게임상 맨위의 공간에 굳은 블록(시스템 상 1, "■"로 표현)이 있는지 테스트
                if(Tetris[4][j] == 1){
                    game_over(); //만약 맨위 공간에 굳은 블록이 있으면 게임 오버함수 호출 후 브레이크
                    break;
                }
            }


        int r = (int)(Math.random() * 7) + 1; //난수로 1부터 7까지 값 랜덤 생성후 random_blocks함수에 인자로 전달
        random_blocks(r);
    }

    static void random_blocks(int r) { //랜덤한 블록을 생성하는 함수
        switch (r) {
            case 1:
                for (int i = 2; i < 6; i++) {// 1부터 7까지의 각각의 형태의 블록을 생성
                    Tetris[4][i] = 2;
                }
                break;

            case 2:
                Tetris[4][5] = 2;
                for (int i = 3; i < 6; i++) {
                    Tetris[5][i] = 2;
                }
                break;

            case 3:
                Tetris[4][3] = 2;
                for (int i = 3; i < 6; i++) {
                    Tetris[5][i] = 2;
                }
                break;

            case 4:
                Tetris[4][4] = 2;
                for (int i = 3; i < 6; i++) {
                    Tetris[4][i] = 2;
                }
                break;

            case 5:
                Tetris[4][4] = 2;
                Tetris[4][5] = 2;
                Tetris[5][4] = 2;
                Tetris[5][5] = 2;
                break;

            case 6:
                Tetris[4][4] = 2;
                Tetris[4][5] = 2;
                Tetris[5][6] = 2;
                Tetris[5][5] = 2;
                break;

            case 7:
                Tetris[4][4] = 2;
                Tetris[4][5] = 2;
                Tetris[5][4] = 2;
                Tetris[5][3] = 2;
                break;
        }
    }

    static void clear_screen() { //화면을 지우는 함수
        System.out.println("\n".repeat(40)); //하지만 인텔리제이로 실행하면 터미널이 아니기 때문에 간단하게 println을 여러번 호출해서 화면 줄 바꿈으로 구현
    }

    static void freeze_blocks(){//현재 조작중인 블록을 고정된 블록 (1, "■")로 만드는 함수
        for(int l = 0;l<col;l++){
            for(int k = 0;k<row;k++){
                if(Tetris[l][k] == 2){
                    Tetris[l][k] = 1;
                }
            }
        }
    }

    static void fall(){ //현재 조작중인 블록을 한 칸씩 아래로 보내는 함수
        int c = col-1;
        int r = row-1;

        for(int i = c; i>0;i--){
            for(int j = r; j>0;j--){
                if(is_down(i,j) && is_in(i,j) && Tetris[i][j] == 2){
                    freeze_blocks();
                    make_blocks();
                    return;
                }

                else if(!is_down(i,j) && is_in(i,j) && Tetris[i][j] == 2){
                    Tetris[i][j] = 0;
                    Tetris[i+1][j] = 2;
                }
            }
        }
    }

    static boolean is_down(int i, int j){//밑에 뭐가 있는지 확인하는 함수
        if(i>=col-1)
            return true;

        if(Tetris[i+1][j] == 1){
            return true;
        }
        return false;
    }

    static boolean is_in(int i, int j){//게임 범위 내에 있는지 확인하는 함수
        if(i <0 || i >col-1 || j < 0 || j >row-1){
            return false;
        }
        return true;
    }

    static void order() throws IOException {

        Terminal terminal = TerminalBuilder.builder()
                .system(false)                 // ★ 시스템 터미널 쓰려고 시도 안 함
                .dumb(true)                    // ★ 처음부터 DumbTerminal 쓰겠다고 명시
                .streams(System.in, System.out) // ★ 표준 입출력 사용
                .build();

        terminal.enterRawMode(); // dumb이어도 호출은 가능 (실제 raw는 안 될 수 있음)

        int ch = terminal.reader().read();  // 엔터 없이 바로 읽힘
        char c = Character.toLowerCase((char) ch);
        int value = switch (c) {
            case 'a' -> 1;
            case 'd' -> 2;
            case 's' -> 3;
            case 'r' -> 4;
            default -> 5;
        };

        move(value);

    }

    static void move(int value){
        switch (value){
            case 1:
                left();
                break;

            case 2:
                right();
                break;

            case 3:
                down();
                break;

            case 4:
                rotate();
                break;

            case 5:
                break;
        }
    }

    static void left(){
        System.out.print("left 테스트");
    }

    static void right(){
        System.out.print("right 테스트");
    }

    static void down(){
        System.out.print("down 테스트");
    }

    static void rotate(){
        System.out.print("rotate 테스트");
    }

    static boolean game_over() { //게임 오버를 판별하고 게임 오버시 게임을 종료시키는 함수 (아직 미구현이라 일단은 false로 해놓음)
        return false;
    }

    public static void main(String[] args) throws InterruptedException, IOException {

        init();
        make_blocks();

        while(true) {
            print_tetris();
            fall();
            TimeUnit.SECONDS.sleep(1);
            //clear_screen();
            try {
                order();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}