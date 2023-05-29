import java.util.Random;
import java.util.Scanner;

public class Main {

    private static final int WIN_COUNT = 4;
    private static final char DOT_HUMAN = 'X';
    private static final char DOT_AI = 'O';
    private static final char DOT_EMPTY = '•';

    private static final Scanner SCANNER = new Scanner(System.in);

    private static char[][] field;

    private static final Random random = new Random();

    private static int fieldSizeX;
    private static int fieldSizeY;


    public static void main(String[] args) {
        while (true) {
            initialize();
            printField();

            while (true) {
                humanTurn();
                printField();
                if (gameCheck(DOT_HUMAN, "Вы победили!"))
                    break;
                aiTurn();
                printField();
                if (gameCheck(DOT_AI, "Компьютер победил!"))
                    break;
            }
            System.out.println("Желаете сыграть еще раз? (Y - да)");
            if (!SCANNER.next().equalsIgnoreCase("Y"))
                break;
        }

    }


    /**
     * Инициализация игрового поля
     */
    private static void initialize() {
        fieldSizeX = 5;
        fieldSizeY = 5;


        field = new char[fieldSizeX][fieldSizeY];

        for (int x = 0; x < fieldSizeX; x++) {
            for (int y = 0; y < fieldSizeY; y++) {
                field[x][y] = DOT_EMPTY;
            }
        }
    }

    /**
     * Отрисовка игрового поля
     */
    private static void printField() {
        System.out.print("+");
        for (int i = 0; i < fieldSizeX * 2 + 1; i++) {
            System.out.print((i % 2 == 0) ? "-" : i / 2 + 1);
        }
        System.out.println();

        for (int i = 0; i < fieldSizeX; i++) {
            System.out.print(i + 1 + "|");

            for (int j = 0; j < fieldSizeY; j++)
                System.out.print(field[i][j] + "|");

            System.out.println();
        }

        for (int i = 0; i < fieldSizeX * 2 + 2; i++) {
            System.out.print("-");
        }
        System.out.println();

    }

    /**
     * Обработка хода игрока (человек)
     */
    private static void humanTurn() {
        int x, y;
        do {
            System.out.print("Введите координаты хода X и Y (от 1 до 5) через пробел >>> ");
            x = SCANNER.nextInt() - 1;
            y = SCANNER.nextInt() - 1;
        }
        while (!isCellValid(x, y) || !isCellEmpty(x, y));
        field[x][y] = DOT_HUMAN;
    }

    /**
     * Проверка, ячейка является пустой
     *
     * @param x
     * @param y
     * @return
     */
    static boolean isCellEmpty(int x, int y) {
        return field[x][y] == DOT_EMPTY;
    }


    /**
     * Проверка корректности ввода
     * (координаты хода не должны превышать размерность массива, игрового поля)
     *
     * @param x
     * @param y
     * @return
     */
    static boolean isCellValid(int x, int y) {
        return x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY;
    }


    /**
     * Ход компьютера
     */
    private static void aiTurn() {
        int x, y;
        do {
            x = random.nextInt(fieldSizeX);
            y = random.nextInt(fieldSizeY);
        }
        while (!isCellEmpty(x, y));
        field[x][y] = DOT_AI;
    }

    /**
     * Проверка победы
     *
     * @param playerSign
     * @return
     */
    public static boolean checkWin(char playerSign) {

        for (int i = 0; i < fieldSizeX; i++) {
            for (int j = 0; j < fieldSizeY; j++) {
                if (field[i][j] == playerSign) {
                    int count = 1;

                    int countHorRight = checkInsideFieldWinHorRight(i, j, playerSign, count);
                    String resultHorizRight = (countHorRight == WIN_COUNT) ? "+++ Победа по горизонтали" : "по горизонтали(вправо) не хватает:"+ (WIN_COUNT-countHorRight);
                    System.out.println(resultHorizRight);

                    int countVertBottom = checkInsideFieldWinVertBottom(i, j, playerSign, count);
                    String resultVertBottom = (countVertBottom == WIN_COUNT) ? "+++ Победа по вертикали" : "по вертикали(вниз) не хватает" + (WIN_COUNT - countVertBottom);
                    System.out.println(resultVertBottom);

                    int countDiagonalBottom = checkInsideFieldWinDiagonalBottom(i, j, playerSign, count);
                    String resultDiagonalBottom = (countDiagonalBottom == WIN_COUNT) ? "+++ Победа по диагонали-вниз" : "по диагонали(вниз) не хватает" + (WIN_COUNT - countDiagonalBottom);
                    System.out.println(resultDiagonalBottom);

                    int countDiagonalTop = checkInsideFieldWinDiagonalTop(i, j, playerSign, count);
                    String resultDiagonalTop = (countDiagonalTop == WIN_COUNT) ? "+++ Победа по диагонали-вверх\n" : "по диагонали(вверх) не хватает" + (WIN_COUNT - countDiagonalTop);
                    System.out.println(resultDiagonalTop);

                    count = Math.max(Math.max(countHorRight,countVertBottom), Math.max(countDiagonalBottom, countDiagonalTop));

                    if(count >= WIN_COUNT){return true;}



                }
            }
        }


        return false;
    }


    public static int checkInsideFieldWinHorRight(int i, int j, char sign, int count) {

        try {
            if (field[i][j + 1] == sign) {  // | field[i][j-1] == sign
                count++;
                int horizon = checkInsideFieldWinHorRight(i, j + 1, sign, count);

                count = horizon;
                return count;
            } else {
//            count = 0; // НУЖЕН ЛИ??
            }

        } catch (RuntimeException e) {
            e.getMessage();
        }


        return count;

    }

    public static int checkInsideFieldWinVertBottom(int i, int j, char sign, int count) {

        try {
            if (field[i + 1][j] == sign) {  // | field[i][j-1] == sign
                count++;
                int vertical = checkInsideFieldWinVertBottom(i + 1, j, sign, count);

                count = vertical;
                return count;
            } else {
//            count = 0; // НУЖЕН ЛИ??
            }

        } catch (RuntimeException e) {
            e.getMessage();
        }


        return count;

    }

    public static int checkInsideFieldWinDiagonalBottom(int i, int j, char sign, int count) {

        try {
            if (field[i + 1][j + 1] == sign) {  // | field[i][j-1] == sign
                count++;
                int diagonal = checkInsideFieldWinDiagonalBottom(i + 1, j + 1, sign, count);

                count = diagonal;
                return count;
            } else {
//            count = 0; // НУЖЕН ЛИ??
            }

        } catch (RuntimeException e) {
            e.getMessage();
        }


        return count;

    }

    public static int checkInsideFieldWinDiagonalTop(int i, int j, char sign, int count) {

        try {
            if (field[i - 1][j + 1] == sign) {  // | field[i][j-1] == sign
                count++;
                int diagonal = checkInsideFieldWinDiagonalTop(i - 1, j + 1, sign, count);

                count = diagonal;
                return count;
            } else {
//            count = 0; // НУЖЕН ЛИ??
            }

        } catch (RuntimeException e) {
            e.getMessage();
        }


        return count;

    }


    /**
     * Проверка на ничью
     *
     * @return TODO: переработать метод проверки
     */
    static boolean checkDraw() {
        for (int x = 0; x < fieldSizeX; x++) {
            for (int y = 0; y < fieldSizeY; y++)
                if (isCellEmpty(x, y)) return false;
        }
        return true;
    }

    /**
     * Метод проверки состояния игры
     *
     * @param playerSign - фишка игрока-пользователя
     * @param str
     * @return
     */
    static boolean gameCheck(char playerSign, String str) {
        if (checkWin(playerSign)) {
            System.out.println(str);
            return true;
        }
        if (checkDraw()) {
            System.out.println("Ничья!");
            return true;
        }

        return false; // Игра продолжается
    }


}

