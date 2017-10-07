//to do:
//pawn en passe
//casteling
//pawn to the end
//print and read moves
//build move tree
//value moves
//



import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class ChessBoard {

    Square[][] board = new Square[8][8];

    public ChessBoard()
    {
        String temp_color= "";
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if ((i + j)%2 == 0)
                {
                    temp_color = "B";
                }
                else
                {
                    temp_color = "W";
                }
                board[i][j] = new Square(i, j, temp_color, this);
            }
        }
    }
}

class Square
{
    int x;
    int y;
    String color;
    ChessBoard cb;

    public Square(int x, int y, String color, ChessBoard cb)
    {
        this.x = x;
        this.y = y;
        if ((x + y)%2 == 0)
        {
            this.color = "B";
        }
        else
        {
            this.color = "W";
        }
        this.cb = cb;
    }

    public Square(int x, int y, ChessBoard cb)
    {
        this.x = x;
        this.y = y;
        this.color = color;
        this.cb = cb;
    }

    public Square(String loc)
    {
        if (loc == "")
        {
            this.x = -1;
            this.y = -1;
            this.cb = null;
            this.color = null;
        }
        else
        {
            this.x = ((int)(char) loc.charAt(0)) - 97;
            this.y = ((int)(char) loc.charAt(1)) - 49;
            this.cb = null;
            this.color = null;
        }

    }

    public String getLocationString()
    {
        //a = 97, 1 = 49
        char first_char = (char) (x+97);
        char second_char = (char) (y + 49);
        return "" + first_char + second_char;
    }
}

class MoveTree
{
    MoveNode root;
    ChessBoard cb;
    Player p1;
    Player p2;
    int max_depth = 0;

    MoveTree(Player p1, Player p2, ChessBoard cb)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.cb = cb;
        this.root = new MoveNode(null, null, this, 2);
    }

    public ArrayList<MoveNode> getLevel(int depth)
    {
        int temp_depth = 1;
        ArrayList<MoveNode> temp_level = new ArrayList<MoveNode>();
        temp_level.add(root);
        while (temp_depth <= depth)
        {
            ArrayList<MoveNode> temp_level2 = new ArrayList<MoveNode>();
            for (int i = 0; i < temp_level.size(); i++)
            {
                temp_level2.addAll(temp_level.get(i).children);
            }
            temp_level = temp_level2;
            temp_depth += 1;
        }

        return temp_level;
    }

    public void buildChildren()
    {
        ArrayList<MoveNode> nodes = getLevel(max_depth);
        for (int i = 0; i < nodes.size(); i++)
        {
            ArrayList<Move> temp_moves = new ArrayList<Move>();
            if (max_depth%2 == 0)
            {
                temp_moves= nodes.get(i).p1.getPossibleMoves(false);
            }
            else
            {
                temp_moves=nodes.get(i).p2.getPossibleMoves(false);
            }

            for(int j = 0; j < temp_moves.size(); j++)
            {
                MoveNode temp_move_node = new MoveNode(temp_moves.get(j), nodes.get(i), this, max_depth%2 +1);
                if (temp_moves.get(j).piecetaken != null && !(temp_moves.get(j).piecetaken.p.equals(temp_moves.get(j).p.p)))
                {
                    temp_move_node.points = temp_moves.get(j).piecetaken.value;
                }
                nodes.get(i).children.add(temp_move_node);
            }
        }
    }
}

class MoveNode
{
    MoveTree mt;
    MoveNode parent;
    ArrayList<MoveNode> children;
    int points;
    int cumulative_points;
    Move m;
    Player p1;//player
    Player p2;//opponent
    int depth;

    MoveNode(Move m, MoveNode parent, MoveTree mt, int player)
    {
        this.m = m;
        this.p1 = new Player(m.p.p, mt.cb);
        this.p2 = new Player(m.p.p.other_p, mt.cb);
        this.p1.other_p = this.p2;
        this.p2.other_p = this.p1;
        this.depth = parent.depth + 1;

        if (m != null)
        {

            if (player == 1)
            {
                p1.test_move(m);
            }
            else
            {
                p2.test_move(m);
            }
        }
    }
}

class Move
{
    Piece p;
    Square end_location = null;
    Piece piecetaken = null;
    Square original_location = null;
    Boolean targetting_own_piece = false;

    public Move(Piece p, Square end_location)
    {
        this.p = p;
        this.end_location = end_location;
        this.original_location = p.position;
    }
}

class Player
{
    ArrayList<Piece> pieces = new ArrayList<Piece>();
    int num = 0;
    Player other_p = null;
    ChessBoard cb = null;
    Piece king;

    Player()
    {}


    Player(int num, ChessBoard cb)
    {
        this.cb = cb;
        this.num = num;

        if (num == 1)
        {
            pieces.add(new Pawn(this, cb.board[0][1]));
            pieces.add(new Pawn(this, cb.board[1][1]));
            pieces.add(new Pawn(this, cb.board[2][1]));
            pieces.add(new Pawn(this, cb.board[3][1]));
            pieces.add(new Pawn(this, cb.board[4][1]));
            pieces.add(new Pawn(this, cb.board[5][1]));
            pieces.add(new Pawn(this, cb.board[6][1]));
            pieces.add(new Pawn(this, cb.board[7][1]));
            pieces.add(new Rook(this, cb.board[0][0]));
            pieces.add(new Knight(this, cb.board[1][0]));
            pieces.add(new Bishop(this, cb.board[2][0]));
            pieces.add(new Queen(this, cb.board[3][0]));
            king = new King(this, cb.board[4][0]);
            pieces.add(king);
            pieces.add(new Bishop(this, cb.board[5][0]));
            pieces.add(new Knight(this, cb.board[6][0]));
            pieces.add(new Rook(this, cb.board[7][0]));
        }

        if (num == 2)
        {
            pieces.add(new Pawn(this, cb.board[0][6]));
            pieces.add(new Pawn(this, cb.board[1][6]));
            pieces.add(new Pawn(this, cb.board[2][6]));
            pieces.add(new Pawn(this, cb.board[3][6]));
            pieces.add(new Pawn(this, cb.board[4][6]));
            pieces.add(new Pawn(this, cb.board[5][6]));
            pieces.add(new Pawn(this, cb.board[6][6]));
            pieces.add(new Pawn(this, cb.board[7][6]));
            pieces.add(new Rook(this, cb.board[0][7]));
            pieces.add(new Knight(this, cb.board[1][7]));
            pieces.add(new Bishop(this, cb.board[2][7]));
            pieces.add(new Queen(this, cb.board[3][7]));
            king = new King(this, cb.board[4][7]);
            pieces.add(king);
            pieces.add(new Bishop(this, cb.board[5][7]));
            pieces.add(new Knight(this, cb.board[6][7]));
            pieces.add(new Rook(this, cb.board[7][7]));
        }
    }

    Player(Player p, ChessBoard cb)//create deep copy
    {
        this.cb = cb;
        this.num = p.num;
        for (int i = 0; i < p.pieces.size(); i++)
        {
            if(p.pieces.get(i) instanceof Pawn)
            {
                this.pieces.add(new Pawn(p.pieces.get(i), this.cb));
            }
            if(p.pieces.get(i) instanceof Rook)
            {
                this.pieces.add(new Rook(p.pieces.get(i), this.cb));
            }
            if(p.pieces.get(i) instanceof Knight)
            {
                this.pieces.add(new Knight(p.pieces.get(i), this.cb));
            }
            if(p.pieces.get(i) instanceof Bishop)
            {
                this.pieces.add(new Bishop(p.pieces.get(i), this.cb));
            }
            if(p.pieces.get(i) instanceof Queen)
            {
                this.pieces.add(new Queen(p.pieces.get(i), this.cb));
            }
            if(p.pieces.get(i) instanceof King)
            {
                this.king = new King(p.pieces.get(i), this.cb);
                this.pieces.add(this.king);
            }
        }
    }

    Player(Player p, ChessBoard cb, Move m, Player other_p)//create deep copy
    {
        this.num = p.num;
        this.other_p = other_p;
        for (int i = 0; i < p.pieces.size(); i++)
        {
            if(p.pieces.get(i) instanceof Pawn)
            {
                this.pieces.add(new Pawn(p.pieces.get(i), this.cb));
            }
            if(p.pieces.get(i) instanceof Rook)
            {
                this.pieces.add(new Rook(p.pieces.get(i), this.cb));
            }
            if(p.pieces.get(i) instanceof Knight)
            {
                this.pieces.add(new Knight(p.pieces.get(i), this.cb));
            }
            if(p.pieces.get(i) instanceof Bishop)
            {
                this.pieces.add(new Bishop(p.pieces.get(i), this.cb));
            }
            if(p.pieces.get(i) instanceof Queen)
            {
                this.pieces.add(new Queen(p.pieces.get(i), this.cb));
            }
            if(p.pieces.get(i) instanceof King)
            {
                this.king = new King(p.pieces.get(i), this.cb);
                this.pieces.add(this.king);
            }
        }

        //System.out.println();
        //System.out.println("Test move");
        for (int i = 0; i < pieces.size(); i++)
        {
            //System.out.println("Piece position: " + pieces.get(i).position.getLocationString() + ", Move original position: " + m.original_location.getLocationString());
            if (pieces.get(i).position.equals(m.original_location))
            {
                this.pieces.get(i).position = m.end_location;
                if (m.piecetaken != null)
                {
                    this.other_p.pieces.remove(m.piecetaken);
                }
            }
        }
    }

    public ArrayList<Move> getAllMoves(boolean own_pieces)
    {
        //System.out.println(" Player " + num+ ", number of pieces: " + pieces.size());

        ArrayList<Move> moves = new ArrayList<Move>();
        for (int i = 0; i < pieces.size(); i++)
        {
            moves.addAll(pieces.get(i).getPossibleMovesForPiece());
        }

        if (own_pieces)
        {}

        else
        {
            //find elegant way
            ArrayList<Move> moves2 = new ArrayList<Move>();

            for (int i = 0; i < moves.size(); i++)
            {
                if (!(moves.get(i).targetting_own_piece))
                {
                    moves2.add(moves.get(i));
                }
            }
            moves = moves2;
        }
        return moves;
    }

    public ArrayList<Move> getPossibleMoves(boolean own_pieces)
    {
        //replace string with tuple for speed improvement
        ArrayList<Move> moves = getAllMoves(false);
        ArrayList<Move> moves2 = new ArrayList<Move>();
        ArrayList<Move> other_p_moves = other_p.getAllMoves(true);
        ArrayList<String> other_p_target_locations = new ArrayList<String>();

        for (int i = 0 ; i < other_p_moves.size(); i ++)
        {
            other_p_target_locations.add(other_p_moves.get(i).end_location.getLocationString());
        }

        for (int i = 0 ; i < moves.size(); i ++)
        {
            Player p2 = new Player(this.other_p, cb);
            Player p1 = new Player(this, cb);
            p1.other_p = p2;
            p2.other_p = p1;

            p1.test_move(moves.get(i));

            Boolean move_valid = true;
            ArrayList<Move> temp_other_p_moves  = p2.getAllMoves(true);
            for (int j = 0; j < temp_other_p_moves.size(); j++)
            {
                if (temp_other_p_moves.get(j).end_location.getLocationString().equals(p1.king.position.getLocationString()))
                {
                    move_valid = false;
                    break;
                }
            }

            if (move_valid)
            {
                moves2.add(moves.get(i));
            }
        }

        //p1.move(moves.get(i).original_location.getLocationString(),moves.get(i).end_location.getLocationString(),moves.get(i).piecetaken.position.getLocationString());
        return moves2;
    }

    private void move(String starting_location, String end_location, String capture_location)//different method
    {
        Square starting_square = new Square(starting_location);
        Square end_square = new Square(end_location);
        Square capture_square = new Square(capture_location);

        getSquareContent(starting_square.x, starting_square.y).position = cb.board[end_square.x][end_square.y];

        if (capture_location != "")
        {
            other_p.pieces.remove(other_p.getSquareContent(capture_square.x, capture_square.y));
        }
    }

    public Piece getSquareContent(int x , int y)
    {
        for (int i = 0; i < pieces.size(); i++)
        {
            if (cb.board[x][y].equals(pieces.get(i).position))
            {
                return pieces.get(i);
            }
        }
        return null;
    }

    public boolean isInCheck()
    {
        ArrayList<Move> other_p_moves = other_p.getAllMoves(true);
        ArrayList<String> other_p_target_locations = new ArrayList<String>();

        for (int i = 0 ; i < other_p_moves.size(); i ++)
        {
            other_p_target_locations.add(other_p_moves.get(i).end_location.getLocationString());
        }

        if (other_p_target_locations.contains(king.position.getLocationString()))
        {
            return true;
        }

        return false;
    }

    public boolean isInCheckmate()
    {
        int possible_moves = getPossibleMoves(true).size();
        //System.out.println();
        //System.out.println("Checkmate test, possible moves: " + possible_moves);

        ArrayList<Move> temp = getPossibleMoves(true);

        if (isInCheck() && possible_moves == 0)
        {
            return true;
        }
        return false;
    }

    public void test_move(Move m)
    {
        for (int i = 0; i < pieces.size(); i++)
        {
            if (pieces.get(i).equals(m.p))
            {
                if (m.piecetaken != null)
                {
                    other_p.pieces.remove(m.piecetaken);
                }

                if (m.p instanceof Pawn && (m.end_location.y == 7 || m.end_location.y == 0))
                {
                    Queen new_piece = new Queen(this, m.end_location);
                    new_piece.movehistory = pieces.get(i).movehistory;
                    pieces.set(i, new_piece);
                }

                pieces.get(i).position = m.end_location;
                pieces.get(i).movehistory.add(m);
            }

            else
            {
                pieces.get(i).movehistory.add(null);
            }
        }
    }

    public void executeMove(Move m)
    {
        for (int i = 0; i < pieces.size(); i++)
        {
            if (pieces.get(i).equals(m.p))
            {
                System.out.printf("Player %d moving piece %s from %s to %s", num, m.p.getClass().getName(), m.original_location.getLocationString(), m.end_location.getLocationString());
                if (m.piecetaken != null)
                {
                    other_p.pieces.remove(m.piecetaken);
                    System.out.println();
                    System.out.printf("Player %d takes piece %s at %s", num, m.piecetaken.getClass().getName(), m.end_location.getLocationString());
                }

                if (m.p instanceof Pawn && (m.end_location.y == 7 || m.end_location.y == 0))
                {
                    Queen new_piece = new Queen(this, m.end_location);
                    new_piece.movehistory = pieces.get(i).movehistory;
                    pieces.set(i, new_piece);
                    System.out.println();
                    System.out.printf("Player %d moves pawn to end and upgraded to queen", num);
                }

                //need to store move
                System.out.println();
                System.out.println();
                pieces.get(i).position = m.end_location;
                pieces.get(i).movehistory.add(m);
            }

            else
            {
                pieces.get(i).movehistory.add(null);
            }
        }
    }

    public void printPieceLocations()
    {
        for (int i = 0; i < pieces.size(); i++)
        {
            System.out.println(pieces.get(i).getClass().toString() + " at " + pieces.get(i).position.getLocationString());
        }
    }

    public int playTurnRandom()//currently random
    {
        if (isInCheckmate())
        {
            System.out.printf("Player %d concedes", num);
            System.out.println();
            System.out.println("King at "+ king.position.getLocationString());
            printPieceLocations();
            System.out.println("");
            System.out.println("Opponent");
            other_p.printPieceLocations();
            return 4;
        }

        ArrayList<Move> moves = getPossibleMoves(false);

        if (isInCheck())
        {
            System.out.printf("Player %d in check", num);
            System.out.println();
        }
        if ((moves.size() == 0 && !(isInCheck())) || (pieces.size() == 1 && other_p.pieces.size() == 1))
        {
            System.out.println("Draw");
            System.out.println();
            return 3;
        }

        Random r = new Random();
        executeMove(moves.get(r.nextInt(moves.size())));
        return 2;
    }

    public void buildPossibilities(int depth)
    {
        MoveTree mt= new MoveTree(this, other_p, cb);
        for (int i = 0; i < depth; i++)
        {
            mt.buildChildren();
        }

    }

    //testing
    Player(int player_num, int testnum, ChessBoard cb)
    {
        if (testnum == 1)
        {
            //test check and checkmate
            this.cb = cb;
            this.num = player_num;

            if (num == 1)
            {
                king = new King(this, cb.board[0][0]);
                pieces.add(king);
            }
            if (num == 2)
            {
                king = new King(this, cb.board[7][7]);
                pieces.add(king);
                pieces.add(new Queen(this, cb.board[2][2]));
                pieces.add(new Bishop(this, cb.board[3][3]));
            }
        }

        if (testnum == 2)
        {
            //test check
            this.cb = cb;
            this.num = player_num;

            if (num == 1)
            {
                king = new King(this, cb.board[0][0]);
                pieces.add(king);
            }
            if (num == 2)
            {
                king = new King(this, cb.board[7][7]);
                pieces.add(king);
                pieces.add(new Queen(this, cb.board[1][1]));
            }
        }
        if (testnum == 3)
        {
            //test check
            this.cb = cb;
            this.num = player_num;
            //rnbq1bnr/3pp3/1ppBk3/p1P2ppp/8/P2PQPP1/RP2P2P/1N2KBNR b - - 0 1
            if (num == 1)
            {
                king = new King(this, "e1",cb);
                pieces.add(king);
                pieces.add(new Pawn(this, "a3",cb));
                pieces.add(new Pawn(this, "b2",cb));
                pieces.add(new Pawn(this, "c5",cb));
                pieces.add(new Pawn(this, "d3",cb));
                pieces.add(new Pawn(this, "e2",cb));
                pieces.add(new Pawn(this, "f3",cb));
                pieces.add(new Pawn(this, "g3",cb));
                pieces.add(new Rook(this, "a2",cb));
                pieces.add(new Knight(this, "b1",cb));
                pieces.add(new Bishop(this, "d6",cb));
                pieces.add(new Queen(this, "e3",cb));
                pieces.add(new Bishop(this, "f1",cb));
                pieces.add(new Knight(this, "g1",cb));
                pieces.add(new Rook(this, "h1",cb));
            }
            if (num == 2)
            {
                king = new King(this, "d6",cb);
                pieces.add(king);
                pieces.add(new Pawn(this, "a5",cb));
                pieces.add(new Pawn(this, "b7",cb));
                pieces.add(new Pawn(this, "c7",cb));
                pieces.add(new Pawn(this, "c4",cb));
                pieces.add(new Pawn(this, "e5",cb));
                pieces.add(new Pawn(this, "f7",cb));
                pieces.add(new Pawn(this, "g6",cb));
                pieces.add(new Rook(this, "c5",cb));
                pieces.add(new Knight(this, "b8",cb));
                pieces.add(new Bishop(this, "c8",cb));
                pieces.add(new Queen(this, "a4",cb));
                pieces.add(new Bishop(this, "f8",cb));
                pieces.add(new Knight(this, "g8",cb));
                pieces.add(new Rook(this, "h3",cb));

            }
        }
    }
}

abstract class Piece
{
    Player p;
    Square position;
    int value;
    ArrayList<Move> movehistory = new ArrayList<Move>();

    Piece(Player p, Square position)
    {
        this.position = position;
        this.p = p;
    }

    Piece(Player p, String position_string, ChessBoard cb)
    {
        int x = ((int)(char) position_string.charAt(0)) - 97;
        int y = ((int)(char) position_string.charAt(1)) - 49;
        this.position = cb.board[x][y];
        this.p = p;
    }

    Piece(Piece piece, ChessBoard cb)
    {
        this.position = new Square(piece.position.x,piece.position.y, cb);
        this.p = piece.p;
        this.value = piece.value;
    }

    public void move(Square new_position)
    {
        this.position = new_position;
    }

    public void remove()
    {
        this.position = null;
    }

    public abstract ArrayList<Move> getPossibleMovesForPiece();
}

class Pawn extends Piece{
    Pawn(Player p, Square position)
    {
        super(p,position);
        this.value = 1;
    }

    Pawn(Piece pawn, ChessBoard cb)
    {
        super(pawn, cb);
    }

    public Pawn(Player player, String position_string, ChessBoard cb) {
        super(player, position_string, cb);
    }

    public ArrayList<Move> getPossibleMovesForPiece()
    {
        ArrayList<Move> moves = new ArrayList<Move>();
        if (this.p.num == 1)
        {
            //determine if piece directly in front of it
            if (this.p.getSquareContent(position.x, position.y + 1) == null && (this.p.other_p.getSquareContent(position.x, position.y + 1) == null))
            {
                moves.add(new Move(this, this.p.cb.board[position.x][position.y + 1]));
                if (position.y ==1 && this.p.getSquareContent(position.x, position.y + 2) == null && (this.p.other_p.getSquareContent(position.x, position.y + 2) == null))
                {
                    moves.add(new Move(this, this.p.cb.board[position.x][position.y + 2]));
                }
            }

            if (position.x != 7 &&this.p.other_p.getSquareContent(position.x + 1, position.y + 1) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x + 1][position.y + 1]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(position.x + 1, position.y + 1);
                moves.add(temp_move);
            }
            if (position.x != 7 &&this.p.getSquareContent(position.x + 1, position.y + 1) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x + 1][position.y + 1]);
                temp_move.targetting_own_piece = true;
                temp_move.piecetaken = this.p.getSquareContent(position.x + 1, position.y + 1);
                moves.add(temp_move);
            }
            if (position.x != 0 && this.p.other_p.getSquareContent(position.x - 1, position.y + 1) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x -1][position.y + 1]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(position.x - 1, position.y + 1);
                moves.add(temp_move);
            }
            if (position.x != 0 && this.p.getSquareContent(position.x - 1, position.y + 1) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x -1][position.y + 1]);
                temp_move.piecetaken = this.p.getSquareContent(position.x - 1, position.y + 1);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
            }
            //en passe
        }

        else
        {
            //determine if piece directly in front of it
            if (this.p.getSquareContent(position.x, position.y - 1) == null && (this.p.other_p.getSquareContent(position.x, position.y - 1) == null))
            {
                moves.add(new Move(this, this.p.cb.board[position.x][position.y - 1]));
                if (position.y ==6 && this.p.getSquareContent(position.x, position.y - 2) == null && (this.p.other_p.getSquareContent(position.x, position.y - 2) == null))
                {
                    moves.add(new Move(this, this.p.cb.board[position.x][position.y - 2]));
                }
            }

            if (position.x < 7 && this.p.other_p.getSquareContent(position.x + 1, position.y - 1) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x + 1][position.y - 1]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(position.x + 1, position.y -1);
                moves.add(temp_move);
            }
            if (position.x > 0 && this.p.other_p.getSquareContent(position.x - 1, position.y - 1) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x - 1][position.y - 1]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(position.x - 1, position.y - 1);
                moves.add(temp_move);
            }
            if (position.x < 7 && this.p.getSquareContent(position.x + 1, position.y - 1) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x + 1][position.y - 1]);
                temp_move.piecetaken = this.p.getSquareContent(position.x + 1, position.y -1);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
            }
            if (position.x > 0 && this.p.getSquareContent(position.x - 1, position.y - 1) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x - 1][position.y - 1]);
                temp_move.piecetaken = this.p.getSquareContent(position.x - 1, position.y - 1);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
            }
        }
        return moves;
    }
}

class Rook extends Piece{
    Rook(Player p, Square position)
    {
        super(p,position);
        this.value = 5;
    }

    Rook(Piece rook, ChessBoard cb)
    {
        super(rook, cb);
    }

    public Rook(Player player, String string, ChessBoard cb) {
        super(player,string, cb);
    }

    public ArrayList<Move> getPossibleMovesForPiece()
    {
        ArrayList<Move> moves = new ArrayList<Move>();

        for (int i = position.y + 1; i < 8 ; i++)
        {
            if (this.p.getSquareContent(position.x, i) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x][i]);
                temp_move.piecetaken = this.p.getSquareContent(position.x, i);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }

            if (this.p.other_p.getSquareContent(position.x, i) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x][i]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(position.x, i);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[position.x][i]));
            }
        }

        for (int i = position.x + 1; i < 8 ; i++)
        {
            if (this.p.getSquareContent(i, position.y) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[i][position.y]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(i, position.y);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }

            if (this.p.other_p.getSquareContent(i, position.y) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[i][position.y]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(i, position.y);
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[i][position.y]));
            }
        }

        for (int i = position.y - 1; i >=0  ; i--)
        {
            if (this.p.getSquareContent(position.x, i) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x][i]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(position.x, i);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }

            if (this.p.other_p.getSquareContent(position.x, i) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x][i]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(position.x, i);
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[position.x][i]));
            }
        }

        for (int i = position.x - 1; i >=0  ; i--)
        {
            if (this.p.getSquareContent(i, position.y) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[i][position.y]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(i, position.y);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }

            if (this.p.other_p.getSquareContent(i, position.y) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[i][position.y]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(i, position.y);
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[i][position.y]));
            }
        }
        return moves;
    }
}

class Bishop extends Piece{
    Bishop(Player p, Square position)
    {
        super(p,position);
        this.value = 3;
    }

    Bishop(Piece bishop, ChessBoard cb)
    {
        super(bishop, cb);
    }

    public Bishop(Player player, String string, ChessBoard cb) {
        super(player,string, cb);
    }

    public ArrayList<Move> getPossibleMovesForPiece()
    {
        ArrayList<Move> moves = new ArrayList<Move>();

        int tempx = position.x + 1;
        int tempy = position.y + 1;

        while (tempx < 8 && tempy < 8)
        {
            //System.out.println(tempx + ", " + tempy);
            if (this.p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }
            if (this.p.other_p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[tempx][tempy]));
            }

            tempx = tempx + 1;
            tempy = tempy + 1;
        }

        tempx = position.x - 1;
        tempy = position.y + 1;

        while (tempx >= 0 && tempy < 8)
        {
            if (this.p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }
            if (this.p.other_p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[tempx][tempy]));
            }

            tempx = tempx - 1;
            tempy = tempy + 1;
        }

        tempx = position.x - 1;
        tempy = position.y - 1;

        while (tempx >= 0 && tempy >= 0)
        {
            if (this.p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }
            if (this.p.other_p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[tempx][tempy]));
            }

            tempx = tempx - 1;
            tempy = tempy - 1;
        }

        tempx = position.x + 1;
        tempy = position.y - 1;

        while (tempx < 8 && tempy >= 0)
        {
            if (this.p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }
            if (this.p.other_p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[tempx][tempy]));
            }

            tempx = tempx + 1;
            tempy = tempy - 1;
        }
        return moves;
    }
}

class Knight extends Piece{
    Knight(Player p, Square position)
    {
        super(p,position);
        this.value = 3;
    }

    Knight(Piece knight, ChessBoard cb)
    {
        super(knight, cb);
    }

    public Knight(Player player, String string, ChessBoard cb) {
        super(player,string, cb);
    }

    public ArrayList<Move> getPossibleMovesForPiece()
    {
        ArrayList<Move> moves = new ArrayList<Move>();

        if ( position.x > 1)
        {
            if (position.y >0 && position.y <7)
            {
                if (this.p.other_p.getSquareContent(position.x - 2, position.y - 1) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x - 2][position.y - 1]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x - 2, position.y - 1);
                    moves.add(temp_move);
                }
                if (this.p.other_p.getSquareContent(position.x - 2, position.y + 1) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x - 2][position.y + 1]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x - 2, position.y + 1);
                    moves.add(temp_move);
                }
                if (this.p.getSquareContent(position.x - 2, position.y - 1) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x - 2][position.y - 1]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x - 2, position.y - 1);

                    moves.add(temp_move);
                }
                if (this.p.getSquareContent(position.x - 2, position.y + 1) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x - 2][position.y + 1]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x - 2, position.y + 1);

                    moves.add(temp_move);
                }
                if (this.p.other_p.getSquareContent(position.x - 2, position.y - 1) == null)
                {
                    moves.add(new Move(this, this.p.cb.board[position.x - 2][position.y - 1]));
                }
                if (this.p.other_p.getSquareContent(position.x - 2, position.y + 1) == null)
                {
                    moves.add(new Move(this, this.p.cb.board[position.x - 2][position.y + 1]));
                }
            }
        }
        if ( position.x < 6)
        {
            if (position.y >0 && position.y <7)
            {
                if (this.p.other_p.getSquareContent(position.x + 2, position.y - 1) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x + 2][position.y - 1]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x + 2, position.y - 1);
                    moves.add(temp_move);
                }
                if (this.p.other_p.getSquareContent(position.x + 2, position.y + 1) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x + 2][position.y + 1]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x + 2, position.y + 1);
                    moves.add(temp_move);
                }
                if (this.p.getSquareContent(position.x + 2, position.y - 1) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x + 2][position.y - 1]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x + 2, position.y - 1);
                    temp_move.targetting_own_piece = true;
                    moves.add(temp_move);
                }
                if (this.p.getSquareContent(position.x + 2, position.y + 1) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x + 2][position.y + 1]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x + 2, position.y + 1);
                    temp_move.targetting_own_piece = true;
                    moves.add(temp_move);
                }
                if (this.p.other_p.getSquareContent(position.x + 2, position.y - 1) == null)
                {
                    moves.add(new Move(this, this.p.cb.board[position.x + 2][position.y - 1]));
                }
                if (this.p.other_p.getSquareContent(position.x + 2, position.y + 1) == null)
                {
                    moves.add(new Move(this, this.p.cb.board[position.x + 2][position.y + 1]));
                }
            }
        }
        if ( position.x < 7)
        {
            if (position.y >1 && position.y <6)
            {
                if (this.p.other_p.getSquareContent(position.x + 1, position.y - 2) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x + 1][position.y - 2]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x + 1, position.y - 2);
                    moves.add(temp_move);
                }
                if (this.p.other_p.getSquareContent(position.x + 1, position.y + 2) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x + 1][position.y + 2]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x + 1, position.y + 2);
                    moves.add(temp_move);
                }
                if (this.p.getSquareContent(position.x + 1, position.y - 2) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x + 1][position.y - 2]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x + 1, position.y - 2);
                    temp_move.targetting_own_piece = true;
                    moves.add(temp_move);
                }
                if (this.p.getSquareContent(position.x + 1, position.y + 2) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x + 1][position.y + 2]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x + 1, position.y + 2);
                    temp_move.targetting_own_piece = true;
                    moves.add(temp_move);
                }
                if (this.p.other_p.getSquareContent(position.x + 1, position.y - 2) == null)
                {
                    moves.add(new Move(this, this.p.cb.board[position.x + 1][position.y - 2]));
                }
                if (this.p.other_p.getSquareContent(position.x + 1, position.y + 2) == null)
                {
                    moves.add(new Move(this, this.p.cb.board[position.x + 1][position.y + 2]));
                }
            }
        }
        if ( position.x >0 )
        {
            if (position.y >1 && position.y <6)
            {
                if (this.p.other_p.getSquareContent(position.x - 1, position.y - 2) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x - 1][position.y - 2]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x - 1, position.y - 2);
                    moves.add(temp_move);
                }
                if (this.p.other_p.getSquareContent(position.x - 1, position.y + 2) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x - 1][position.y + 2]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x - 1, position.y + 2);
                    moves.add(temp_move);
                }
                if (this.p.getSquareContent(position.x - 1, position.y - 2) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x - 1][position.y - 2]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x - 1, position.y - 2);
                    temp_move.targetting_own_piece = true;
                    moves.add(temp_move);
                }
                if (this.p.getSquareContent(position.x - 1, position.y + 2) != null)
                {
                    Move temp_move = new Move(this, this.p.cb.board[position.x - 1][position.y + 2]);
                    temp_move.piecetaken = this.p.other_p.getSquareContent(position.x - 1, position.y + 2);
                    temp_move.targetting_own_piece = true;
                    moves.add(temp_move);
                }
                if (this.p.other_p.getSquareContent(position.x - 1, position.y - 2) == null)
                {
                    moves.add(new Move(this, this.p.cb.board[position.x - 1][position.y - 2]));
                }
                if (this.p.other_p.getSquareContent(position.x - 1, position.y + 2) == null)
                {
                    moves.add(new Move(this, this.p.cb.board[position.x - 1][position.y + 2]));
                }
            }
        }
        return moves;
    }
}

class King extends Piece{
    King(Player p, Square position)
    {
        super(p,position);
    }

    King(Piece king, ChessBoard cb)
    {
        super(king, cb);
    }

    public King(Player player, String string, ChessBoard cb) {
        super(player,string, cb);
    }

    public ArrayList<Move> getPossibleMovesForPiece()
    {
        ArrayList<Move> moves = new ArrayList<Move>();
        if(position.x >=0)
        {
            if (position.x > 0)
            {
                moves.add(new Move(this, this.p.cb.board[position.x - 1][position.y]));

                if (position.y >0)
                {
                    moves.add(new Move(this, this.p.cb.board[position.x - 1][position.y - 1]));
                    moves.add(new Move(this, this.p.cb.board[position.x][position.y - 1]));
                }

                if (position.y <7)
                {
                    moves.add(new Move(this, this.p.cb.board[position.x - 1][position.y + 1]));
                    moves.add(new Move(this, this.p.cb.board[position.x][position.y + 1]));
                }

            }
            else
            {
                if (position.y >0)
                {
                    moves.add(new Move(this, this.p.cb.board[position.x][position.y - 1]));
                }

                if (position.y <7)
                {
                    moves.add(new Move(this, this.p.cb.board[position.x][position.y + 1]));
                }
            }
        }

        if(position.x <7)
        {
            moves.add(new Move(this, this.p.cb.board[position.x + 1][position.y]));

            if (position.y >0)
            {
                moves.add(new Move(this, this.p.cb.board[position.x + 1][position.y - 1]));
            }

            if (position.y <7)
            {
                moves.add(new Move(this, this.p.cb.board[position.x + 1][position.y + 1]));
            }
        }

        for (int i = 0; i < moves.size(); i++)
        {
            if (this.p.other_p.getSquareContent(moves.get(i).end_location.x, moves.get(i).end_location.y) != null)
            {
                moves.get(i).piecetaken = this.p.other_p.getSquareContent(moves.get(i).end_location.x, moves.get(i).end_location.y);
            }
            else if (this.p.getSquareContent(moves.get(i).end_location.x, moves.get(i).end_location.y) != null)
            {
                moves.get(i).piecetaken = this.p.getSquareContent(moves.get(i).end_location.x, moves.get(i).end_location.y);
                moves.get(i).targetting_own_piece = true;
            }
        }

        return moves;
    }
}

class Queen extends Piece{
    Queen(Player p, Square position)
    {
        super(p,position);
        this.value = 10;
    }

    Queen(Piece queen, ChessBoard cb)
    {
        super(queen, cb);
    }

    public Queen(Player player, String string, ChessBoard cb) {
        super(player,string, cb);
    }

    public ArrayList<Move> getPossibleMovesForPiece()
    {
        ArrayList<Move> moves = new ArrayList<Move>();
        for (int i = position.y + 1; i < 8 ; i++)
        {
            if (this.p.getSquareContent(position.x, i) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x][i]);
                temp_move.piecetaken = this.p.getSquareContent(position.x, i);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }

            if (this.p.other_p.getSquareContent(position.x, i) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x][i]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(position.x, i);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[position.x][i]));
            }
        }

        for (int i = position.x + 1; i < 8 ; i++)
        {
            if (this.p.getSquareContent(i, position.y) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[i][position.y]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(i, position.y);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }

            if (this.p.other_p.getSquareContent(i, position.y) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[i][position.y]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(i, position.y);
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[i][position.y]));
            }
        }

        for (int i = position.y - 1; i >=0  ; i--)
        {
            if (this.p.getSquareContent(position.x, i) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x][i]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(position.x, i);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }

            if (this.p.other_p.getSquareContent(position.x, i) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[position.x][i]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(position.x, i);
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[position.x][i]));
            }
        }

        for (int i = position.x - 1; i >=0  ; i--)
        {
            if (this.p.getSquareContent(i, position.y) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[i][position.y]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(i, position.y);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }

            if (this.p.other_p.getSquareContent(i, position.y) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[i][position.y]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(i, position.y);
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[i][position.y]));
            }
        }

        int tempx = position.x + 1;
        int tempy = position.y + 1;

        while (tempx < 8 && tempy < 8)
        {
            //System.out.println(tempx + ", " + tempy);
            if (this.p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }
            if (this.p.other_p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[tempx][tempy]));
            }

            tempx = tempx + 1;
            tempy = tempy + 1;
        }

        tempx = position.x - 1;
        tempy = position.y + 1;

        while (tempx >= 0 && tempy < 8)
        {
            if (this.p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }
            if (this.p.other_p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[tempx][tempy]));
            }

            tempx = tempx - 1;
            tempy = tempy + 1;
        }

        tempx = position.x - 1;
        tempy = position.y - 1;

        while (tempx >= 0 && tempy >= 0)
        {
            if (this.p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }
            if (this.p.other_p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[tempx][tempy]));
            }

            tempx = tempx - 1;
            tempy = tempy - 1;
        }

        tempx = position.x + 1;
        tempy = position.y - 1;

        while (tempx < 8 && tempy >= 0)
        {
            if (this.p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                temp_move.targetting_own_piece = true;
                moves.add(temp_move);
                break;
            }
            if (this.p.other_p.getSquareContent(tempx, tempy) != null)
            {
                Move temp_move = new Move(this, this.p.cb.board[tempx][tempy]);
                temp_move.piecetaken = this.p.other_p.getSquareContent(tempx, tempy);
                moves.add(temp_move);
                break;
            }
            else
            {
                moves.add(new Move(this, this.p.cb.board[tempx][tempy]));
            }

            tempx = tempx + 1;
            tempy = tempy - 1;
        }
        return moves;
    }
}