package TicTacToe;

import Util.Point;

import java.text.ParseException;
import java.util.ArrayList;

import Util.Util;
import Util.Maybe;

/**
 *
 */
public class Game {
	private Board board;

	public static class Move{
		private Point<Integer> position;
		private Mark mark;

		@Override
		public String toString(){
			return "Move(" + this.position + "," + this.mark + ")";
		}

		public static boolean isParseable(final String IN){
			try{
				Move move = parse(IN);
			} catch(ParseException e){
				return false;
			}
			return true;
		}

		public static Move parse(final String IN) throws ParseException{
			String in = IN.trim();
			final String CLASS_LABEL = "Move";
			final String SPLITTER = ",";
			final char[] TO_REMOVE = {'(',')'};
			for(char a: TO_REMOVE){
				in = in.replace(a,' ');
			}
			in = in.replaceAll(CLASS_LABEL,"");
			String[] remaining = in.split(SPLITTER);
			Maybe<Integer> x = new Maybe<Integer>(), y = new Maybe<Integer>();
			Maybe<Mark> mark = new Maybe<Mark>();
			for(String a: remaining){
				a = a.trim();
				if(Util.isInt(a)){
					if(!x.isValid()){
						x.set(Integer.parseInt(a));
					} else if(!y.isValid()){
						y.set(Integer.parseInt(a));
					}
				}
				try{
					mark.set(Mark.valueOf(a));
				} catch(IllegalArgumentException e){
					//ignore it
				}
			}
			if(x.isValid() && y.isValid() && mark.isValid()){
				return new Move(new Point<Integer>(x.get(),y.get()),mark.get());
			}
			throw new ParseException(IN,0);//TODO?
		}

		public Point<Integer> getPosition(){
			return this.position;
		}

		public Mark getMark(){
			return this.mark;
		}

		public Move(Point<Integer> position, Mark mark){
			this.position = position;
			this.mark = mark;
		}
	}

	private ArrayList<Move> moves;

	public static class Players{
		private enum ActivePlayer{
			_1,_2;

			public static ActivePlayer toggle(ActivePlayer activePlayer){
				switch(activePlayer){
					case _1:
						return _2;
					case _2:
						return _1;
					default:
						Util.nyi(Util.getFileName(),Util.getLineNumber());
				}
				return null;//will never reach this line
			}
		}
		private ActivePlayer activePlayer;
		private Player player1;
		private Player player2;

		public Player getActivePlayer(){
			switch(this.activePlayer){
				case _1:
					return this.player1;
				case _2:
					return this.player2;
				default:
					Util.nyi(Util.getFileName(),Util.getLineNumber());
			}
			return null;//will never reach this line
		}

		@Override
		public String toString(){
			return "Players(player1:" + player1.toString() + " player2:" + player2.toString() + " activePlayer:" + this.activePlayer.toString() + ")";
		}

		public void toggleActivePlayer(){
			this.activePlayer = ActivePlayer.toggle(this.activePlayer);
		}

		public Player getPlayer1(){
			return this.player1;
		}

		public Player getPlayer2(){
			return this.player2;
		}

		public Players(Player player1, Player player2){
			this.player1 = player1;
			this.player2 = player2;
			this.activePlayer = ActivePlayer._1;
		}
	}

	private Players players;

	private void runTurn(Player player){
		System.out.println(this.board.toPrintable());
		System.out.print("Insert a position to add " + this.board.getNextMark().toString() + ": ");
		Move move = player.getMove(this.board);
		this.board.set(move.getPosition());
		moves.add(move);
	}

	public void play(){
		while(this.board.getStatus() == Board.Status.UNFINISHED){
			runTurn(this.players.getActivePlayer());
			this.players.toggleActivePlayer();
		}
		System.out.println(this.board.toPrintable());
		System.out.println("Game finished with: " + this.board.getStatus());
	}

	public Board getBoard(){
		return this.board;
	}

	public ArrayList<Move> getMoves(){
		return this.moves;
	}

	public Game(){
		this(new ArrayList<>(), Board.Status.UNFINISHED);
	}

	public Game(ArrayList<Move> moves, Board.Status status){
		this.board = new Board(status);
		this.moves = moves;
		this.players = new Players(new AI(), new AI());
	}

	@Override
	public String toString(){
		return "Game(moves:" + this.moves.toString() + " board:" + this.board.toString() + " players:" + this.players.toString() + ")";
	}

	public static void store(Game game){
		Database.writeGame(game);
	}

	public static void main(String[] args){
		Game game = new Game();
		game.play();
		store(game);
		for(Game a: Database.readGames()){
			System.out.println(a);
		}
	}
}
