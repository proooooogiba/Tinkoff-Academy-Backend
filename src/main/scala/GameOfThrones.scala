class GameOfThrones(
    val targaryen: Targaryen,
    val lannisters: Lannisters,
    private val moveNumber: Int = 0
) {

  def nextTurn(targaryenMove: Wealth)(lannistersMove: Wealth): GameOfThrones = {
    new GameOfThrones(
      Targaryen(wealth = targaryenMove),
      Lannisters(wealth = lannistersMove),
      moveNumber + 1
    )
  }
}
