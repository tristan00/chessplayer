import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args)
    {
        //playRandom();
        testCheckmate();
    }


    static void play1()
    {
        ChessBoard board = new ChessBoard();
        Player p1 = new Player(1, board);
        Player p2 = new Player(2,  board);
        p1.other_p = p2;
        p2.other_p = p1;

        while (true)
        {

        }
    }

    static void playRandom()
    {
        ChessBoard board = new ChessBoard();
        Player p1 = new Player(1, board);
        Player p2 = new Player(2,  board);
        p1.other_p = p2;
        p2.other_p = p1;

        while (true)
        {
            if (p1.playTurnRandom() != 2)
            {
                break;
            }
            if (p2.playTurnRandom() != 2)
            {
                break;
            }
        }

    }

    static void testCheckmate()
    {
        ChessBoard board = new ChessBoard();
        Player p1 = new Player(1, 3, board);
        Player p2 = new Player(2, 3,  board);
        p1.other_p = p2;
        p2.other_p = p1;

        System.out.println(p2.isInCheck());
        System.out.println(p2.isInCheckmate());
    }


}