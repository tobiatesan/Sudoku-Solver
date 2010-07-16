package sudokusolver

import scala.collection.mutable.LinkedList

class Sudoku {
	private var numberFields: Array[Int] = new Array[Int](81)
    private var stepList: List[utilities.Step] = List[utilities.Step]()

    def addStep(couple: utilities.Couple, value: Int) {
      var step : utilities.Step = new utilities.Step()
      this.stepList ::= step
    }
    
    def extractStep() {
      this.stepList.head
    }
    
    def notifyView() {
      //TODO
    }
}