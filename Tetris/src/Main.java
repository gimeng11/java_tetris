import java.util.concurrent.TimeUnit;

public class Main {

    static int col = 25;
    static int row = 10;
    private static int[][] Tetris = new int[col][row];

    static void init() {
        for (int i = 0; i < col; i++) {
            for (int j = 0; j < row; j++) {
                Tetris[i][j] = 0;
            }
        }
    }

    static void print_tetris() {

        for (int i = 0; i < col; i++) {
            for (int j = 0; j < row; j++) {
                if (Tetris[i][j] == 0)
                    System.out.print(" □ ");
                else if(Tetris[i][j] == 2)
                    System.out.print(" ■ ");
            }
            System.out.println();
        }
    }

    static void make_blocks() {

            for (int j = 0; j < 10; j++) {
                if(Tetris[4][j] == 1){
                    game_over();
                    break;
                }
            }


        int r = (int)(Math.random() * 7) + 1;
        random_blocks(r);
    }

    static void random_blocks(int r) {
        switch (r) {
            case 1:
                for (int i = 2; i < 6; i++) {
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
                Tetris[5][5] = 2;
                break;
        }
    }

    static void clear_screen() {
        System.out.println("\n".repeat(40));
    }

    static void fall(){

    }

    static boolean game_over() {
        return false;
    }

    public static void main(String[] args) throws InterruptedException {

        init();
        make_blocks();

        while(true) {
            print_tetris();
            TimeUnit.SECONDS.sleep(1);
            clear_screen();
        }
    }
}