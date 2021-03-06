package sudokusolver

/**
 * This class represents the game with the fields of the scheme.
 */

class Sudoku(private var constraints : utilities.BinaryConstraintContainer) {
	class InvalidValueException extends Exception

	private var board = new utilities.Board
	private var domains = new utilities.DomainContainer
    private var stepList = List[utilities.Step]()
    private var views = List[View]()

    /**
     * Sets a variable in the board, storing a new step in steplist for
     * backtracking purposes.
     * 
     * @param x the row of the board
     * @param y the column of the board
     * @param value the actual value
     * @param message a description of the action
     */
	def set(x: Int, y: Int, value: Int, message : String) {
		if (value < 1 || value > 9) throw new InvalidValueException()
		board.setValue(x, y, value)
		val step = new utilities.Step(new utilities.Couple(x, y), value, message, domains.clone)
		domains.set(new sudokusolver.utilities.Domain("(" + x + "," + y + ")"), x, y)
		domains.get(x, y).empty
		domains.get(x, y).addValue(value)
		stepList = stepList :+ step
		Core.log("(" + x + "," + y + ") = " + value + " : " + message) 

		notifyView
	}
	
	def set(item : utilities.Couple, value : Int, message : String) {
		set(item.getX, item.getY, value, message)
	}
	
	/**
	 * Sets the entire board, initializing the domains accordingly
	 * 
	 * @param newBoard a 9x9 matrix of int values representing the board
	 * 		(use 0 for empty cells)
	 */
	def setBoard(newBoard: utilities.Board) {
		board = newBoard
		domains = new utilities.DomainContainer
		for (i <- 0 to 8) {
			for (j <- 0 to 8) {
				if (board.isNotNull(i, j)) {
					domains.get(i, j).empty
					domains.get(i, j).addValue(board.getValue(i, j))
				}
			}
		}
		notifyView
	}
	
	def getBoard = board
	
	/**
	 * Gets an element from the board
	 */
    def get(x: Int, y: Int) = board.getValue(x, y)
    
    /**
     * Applies backtracking for one step
     */
    def back {
    	val step = stepList.head
    	stepList = stepList - step
    	board.setNull(step.getCouple)
    	domains = step.getDomains
    	
    	notifyView
    }
    
    /**
     * Resets the game to its initial status
     */
    def reset {
    	while (! stepList.isEmpty)
    		back
    	
    	domains = new utilities.DomainContainer
		for (i <- 0 to 8) {
			for (j <- 0 to 8) {
				if (board.isNotNull(i, j)) {
					domains.get(i, j).empty
					domains.get(i, j).addValue(board.getValue(i, j))
				}
			}
		}
    	//println("Domini: ")
    	//domains.printStatus
    	notifyView
    }

    /**
     * @return the last step
     */
    def getLastStep: utilities.Step = stepList.head

    /**
     * Returns the domain for a single element of the board
     */
    def getDomain(x: Int, y: Int) : utilities.Domain = domains.get(x, y)
    
    def getDomain(item : utilities.Couple) : utilities.Domain = getDomain(item.getX, item.getY)

    /**
     * Returns all the domains stored
     */
    def getDomains() = domains
    
    def setDomains(domains : utilities.DomainContainer) {
    	this.domains = domains
    }

    /**
     * Notifies the views attached to this game
     */
    def notifyView {
    	for (view <- views)
    		view.update
    }

    /**
     * TODO
     * @param view
     */
    def attachView(view : View) {
    	views = view::views
    	view.update
    }

    def checkConstraints: Boolean = {
    	for (row <- 0 to 8) {
	    	for (i <- 0 to 7) {
	    		for (j <- i+1 to 8) {
	    			if (board.areEquals(row, i, row, j))
	    				return false
	    		}
	    	}
    	}
	    for (col <- 0 to 8) {
	    	for (i <- 0 to 7) {
	    		for (j <- i+1 to 8) {
	    			if (board.areEquals(col, i, col, j))
	    				return false
	    		}
	    	}
	    }
	    for (n <- 0 to 2) {
	    	for (m <- 0 to 2) {
	    		for (i <- 0 to 7) {
	    			for (j <- i + 1 to 8) {
	    				if (board.areEquals((n*3 + i/3),(m*3 + i%3),(n*3 + j/3),(m*3 + j%3)))
	    					return false
	    			}
	    		}
	    	}
	    }
	    return true
    }
    
    def getConstraints() = constraints
    
    def printDomainStatus() = domains.printStatus
    
    def printBoardStatus() = board.printStatus
    
    override def clone() = {
    	var newSudoku = new Sudoku(Core.getConstraintMatrix)
    	newSudoku.setBoard(this.getBoard.clone)
    	newSudoku.setDomains(this.getDomains.clone)
    	newSudoku
    }
}