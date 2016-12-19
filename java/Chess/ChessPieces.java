package Chess;

import MySystem.*;

/**
 * Stores an array of pieces and useful methods associated with them
 */
public class ChessPieces{
	private ChessPiece[] pieces;
	private ChessPiece.Move lastMove;
	private ChessPiece lastCapture;

	public void updateAllPossibleMoves(ChessPiece.Color color){
		for(ChessPiece chessPiece: this.pieces){
			if(chessPiece.getColor() == color) chessPiece.updatePossibleMoves(this);
		}
	}

	public void updateAllMoves(){
		for(ChessPiece chessPiece: this.pieces){
			chessPiece.updatePossibleMoves(this);
			chessPiece.limitMovesToLeavingCheck(this);
		}
	}

	public boolean isUnoccupied(ChessPosition checkPosition,ChessPiece.Color color){
		for(ChessPiece a: this.pieces){
			if(a.getAlive() && a.getType() != ChessPiece.Type.UNASSIGNED && color == a.getColor() && checkPosition.equals(a.getPosition())) return false;
		}
		return true;
	}

	public boolean isUnoccupied(ChessPosition checkPosition){
		for(ChessPiece a: this.pieces){
			if(a.getAlive() && a.getType() != ChessPiece.Type.UNASSIGNED && checkPosition.equals(a.getPosition())) return false;
		}
		return true;
	}

	public boolean isOccupied(ChessPosition checkPosition,ChessPiece.Color color){
		return !this.isUnoccupied(checkPosition,color);
	}

	@Override
	public String toString(){
		String string = "ChessPieces(";
		for(ChessPiece a: this.pieces){
			string += " " + a.toString();
		}
		string += ")";
		return string;
	}

	/**
	 * Checks if a given piece exists in an array of pieces
	 * @param TEST_PIECE the piece which is checked for existence
	 * @return true if the piece exists in the array
	 */
	public boolean containsLiving(final ChessPiece TEST_PIECE){
		if(!TEST_PIECE.getAlive()) return false;
		for(ChessPiece a: this.pieces){
			if(a.getAlive() && TEST_PIECE.equalsByType(a)) return true;
		}
		return false;
	}

	/**
	 * Used to copy an array of pieces containbed by ChessPieces by value
	 * @param CHESS_PIECES the array to copy
	 * @return the new array
	 */
	public static ChessPieces makePieces(final ChessPieces CHESS_PIECES){
		ChessPieces newPieces = new ChessPieces(CHESS_PIECES.length());
		for(int i = 0; i < CHESS_PIECES.length(); i++){
			newPieces.set(i,ChessPiece.makePiece(CHESS_PIECES.getPieceAt(i)));
		}
		return newPieces;
	}
	/*
	/**
	 * Used to copy an array of pieces by value
	 * @param CHESS_PIECES the array to copy
	 * @return the new array
	 */
	/*public static ChessPiece[] makePieces(final ChessPiece[] CHESS_PIECES){
		ChessPiece[] newPieces = new ChessPiece[CHESS_PIECES.length];
		for(int i = 0; i < CHESS_PIECES.length; i++){
			newPieces[i] = ChessPiece.makePiece(CHESS_PIECES[i]);
		}
		return newPieces;
	}*/

	public ChessPiece.Move getLastMove(){
		return this.lastMove;
	}

	public ChessPiece getLastCapture(){
		return this.lastCapture;
	}

	public boolean equals(Object o){
		if(o.getClass() != this.getClass()) return false;
		ChessPieces b = (ChessPieces)o;
		if(!b.getLastMove().equals(this.getLastMove())) return false;
		if(!b.getLastCapture().equals(this.getLastCapture())) return false;
		if(b.toArray().length != this.toArray().length) return false;
		for(int i = 0; i<this.toArray().length; i++){
			if(!b.toArray()[i].equalsByType(this.toArray()[i])){
				MySystem.println(i + " is not equal " + this.toArray()[i] + b.toArray()[i],MySystem.getFileName(),MySystem.getLineNumber());
				return false;
			}
		}
		return true;
	}

	/**
	 * Finds the location of a piece that meets certain criteria in an array. It crashes if it cannot be found.
	 * @param color the color of the piece
	 * @return the index of the piece if it exits
	 */
	public int getIndexOfKing(ChessPiece.Color color){
		for(int i = 0; i < this.pieces.length; i++){
			if(this.pieces[i].getType() == ChessPiece.Type.KING && color == this.pieces[i].getColor()) return i;
		}
		MySystem.error("King of color " + color.toString() + " not found in array", MySystem.getFileName(), MySystem.getLineNumber());
		return -1;
	}

	/**
	 * Sets a the piece at a given index to a given value
	 * @param INDEX the index of the piece to set
	 * @param CHESS_PIECE the piece to set it to
	 */
	private void set(final int INDEX,final ChessPiece CHESS_PIECE){
		if(!this.isUnoccupied(CHESS_PIECE.getPosition())){
			MySystem.error("Trying to set a piece to a position that is already occupied: piece " + CHESS_PIECE.toString() + " occupying piece: " + this.getPieceAt(CHESS_PIECE.getPosition()),MySystem.getFileName(), MySystem.getLineNumber());
		}
		this.pieces[INDEX] = ChessPiece.makePiece(CHESS_PIECE);
	}

	public void move(final int INDEX,final ChessPosition MOVE_THERE){//TODO: maybe add auto-capturing in here?
		MySystem.myAssert(this.isUnoccupied(MOVE_THERE), MySystem.getFileName(), MySystem.getLineNumber());
		this.lastMove = new ChessPiece.Move(ChessPiece.makePiece(this.pieces[INDEX]), MOVE_THERE);
		this.pieces[INDEX].move(MOVE_THERE);
	}

	public void unMove(){//TODO: add in un-capturing
		MySystem.myAssert(!this.lastMove.equals(new ChessPiece.Move()),MySystem.getFileName(),MySystem.getLineNumber());
		this.set(this.getIndexOfAlive(lastMove.getTarget()),ChessPiece.makePiece(lastMove.getStart()));
		if(!this.lastCapture.equals(new ChessPiece()) && this.lastCapture.getPosition().equals(this.lastMove.getTarget())){
			this.set(this.getIndexOf(this.lastCapture.getPosition()),ChessPiece.makePiece(this.lastCapture));
		}
	}

	/**
	 * Returns the index of a piece in an array which occupies a given position. It crashes if it cannot be found. This one also ensures that the piece that is occupying it is alive
	 * @param chessPosition the position to look for
	 * @return the index of the piece if it exits
	 */
	public int getIndexOfAlive(ChessPosition chessPosition){//TODO: use this in more places? change how alive/captured works?
		for(int i = 0; i < this.pieces.length; i++){
			if(chessPosition.equals(this.pieces[i].getPosition()) && this.pieces[i].getAlive()) return i;
		}
		MySystem.error("Piece not found in array", MySystem.getFileName(), MySystem.getLineNumber());
		return -1;
	}

	/**
	 * Returns the index of a piece in an array which occupies a given position. It crashes if it cannot be found.
	 * @param chessPosition the position to look for
	 * @return the index of the piece if it exits
	 */
	public int getIndexOf(ChessPosition chessPosition){
		for(int i = 0; i < this.pieces.length; i++){
			if(chessPosition.equals(this.pieces[i].getPosition())) return i;
		}
		MySystem.error("Piece not found in array", MySystem.getFileName(), MySystem.getLineNumber());
		return -1;
	}

	public ChessPiece getPieceAt(final int INDEX){
		return this.pieces[INDEX];
	}

	public ChessPiece getPieceAt(final ChessPosition POSITION){
		return this.getPieceAt(this.getIndexOf(POSITION));
	}

	/**
	 * Fetches all of the pieces on the Chess board
	 * @return the array of all the Chess pieces
	 */
	public ChessPiece[] toArray(){
		return this.pieces;
	}

	/**
	 * Fetches the king of the appropriate color
	 * @param color the color of the king to get
	 * @return the king piece of the given color
	 */
	public King getKing(ChessPiece.Color color){
		return new King(this.getPieceAt(this.getIndexOfKing(color)));
	}

	/**
	 * Finds a king and then updates just its check status
	 */
	public void updateKingCheck(ChessPiece.Color color){
		((King)this.getPieceAt(this.getIndexOfKing(color))).updateCheck(this);
	}

	/**
	 * Finds the kings and then updates their check and checkmate statuses
	 */
	public void updateKings(){
		((King)this.getPieceAt(this.getIndexOfKing(ChessPiece.Color.WHITE))).update(this);
		((King)this.getPieceAt(this.getIndexOfKing(ChessPiece.Color.BLACK))).update(this);
	}

	/**
	 * Kills the piece occupying a given Chess position
	 * @param CAPTURING_POSITION the position of the piece to be killed
	 */
	public void capture(ChessPosition CAPTURING_POSITION){
		this.lastCapture = ChessPiece.makePiece(this.pieces[this.getIndexOfAlive(CAPTURING_POSITION)]);
		this.pieces[this.getIndexOfAlive(CAPTURING_POSITION)].capture();
	}

	/**
	 * Find the index of the first Chess piece in an array that hasn't been assigned an identity
	 * @return the index of the next unassigned piece
	 */
	private int findNextUnassigned(){
		for(int i = 0; i < this.pieces.length; i++){
			if(this.pieces[i].getType() == ChessPiece.Type.UNASSIGNED) return i;
		}
		MySystem.error("Error: Unable to find index of the next unassigned piece, array full", MySystem.getFileName(), MySystem.getLineNumber());
		return -1;
	}

	/**
	 * Checks to make sure that position being a assigned a piece is not already occupied any other piece
	 * @param PIECE the piece to be assigned into the array
	 */
	public void setNextPiece(final ChessPiece PIECE){
		MySystem.myAssert(this.isUnoccupied(PIECE.getPosition()), MySystem.getFileName(), MySystem.getLineNumber());
		this.pieces[this.findNextUnassigned()] = ChessPiece.makePiece(PIECE);
	}

	/**
	 * Fetches the length of the pieces array
	 * @return the length of the pieces array
	 */
	public int length(){
		return this.pieces.length;
	}

	public ChessPieces(){
		this(0);
	}

	public ChessPieces(final int LENGTH){
		this.pieces = new ChessPiece[LENGTH];
		for(int i = 0; i< pieces.length; i++){
			this.pieces[i] = new ChessPiece();
		}
		this.lastMove = new ChessPiece.Move();
		this.lastCapture = new ChessPiece();
	}

	public ChessPieces(ChessPiece[] pieces){
		this.pieces = pieces;
		this.lastMove = new ChessPiece.Move();
		this.lastCapture = new ChessPiece();
	}

	public ChessPieces(ChessPieces toCopy){
		this.pieces = ChessPieces.makePieces(toCopy).toArray();
		this.lastMove = new ChessPiece.Move(toCopy.getLastMove());
		this.lastCapture = new ChessPiece();
	}
}
