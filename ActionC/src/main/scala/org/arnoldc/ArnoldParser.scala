package org.arnoldc

import org.parboiled.scala._
import org.parboiled.errors.{ErrorUtils, ParsingException}
import org.arnoldc.ast._

class ArnoldParser extends Parser {


  val ParseError = "WHAT THE FUCK DID I DO WRONG"

  val DeclareInt = "HEY CHRISTMAS TREE"
  val SetInitialValue = "YOU SET US UP"
  val BeginMain = "IT'S SHOWTIME"
  val PlusOperator = "GET UP"
  val MinusOperator = "GET DOWN"
  val MultiplicationOperator = "YOU'RE FIRED"
  val DivisionOperator = "HE HAD TO SPLIT"
  val EndMain = "YOU HAVE BEEN TERMINATED"
  val Print = "TALK TO THE HAND"
  val Read = "I WANT TO ASK YOU A BUNCH OF QUESTIONS AND I WANT TO HAVE THEM ANSWERED IMMEDIATELY"
  val AssignVariable = "GET TO THE CHOPPER"
  val SetValue = "HERE IS MY INVITATION"
  val EndAssignVariable = "ENOUGH TALK"
  val False = "I LIED"
  val True = "NO PROBLEMO"
  val EqualTo = "YOU ARE NOT YOU YOU ARE ME"
  val NotEqual = "IT'S JUST BEEN REVOKED"
  val GreaterThan = "LET OFF SOME STEAM BENNET"
  val LessThan = "YOU'RE THE DISEASE AND I'M THE CURE"
  val GreaterOrEqual = "I'M GETTING TOO OLD FOR THIS"
  val LessOrEqual = "BENEATH YOU"
  val Or = "CONSIDER THAT A DIVORCE"
  val And = "KNOCK KNOCK"
  val Not = "NEGATIVE"
  val If = "BECAUSE I'M GOING TO SAY PLEASE"
  val Else = "BULLSHIT"
  val EndIf = "YOU HAVE NO RESPECT FOR LOGIC"
  val While = "STICK AROUND"
  val EndWhile = "CHILL"
  val DeclareMethod = "LISTEN TO ME VERY CAREFULLY"
  val MethodArguments = "I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE"
  val Return = "I'LL BE BACK"
  val EndMethodDeclaration = "HASTA LA VISTA, BABY"
  val CallMethod = "DO IT NOW"
  val NonVoidMethod = "GIVE THESE PEOPLE AIR"
  val AssignVariableFromMethodCall = "GET YOUR ASS TO MARS"
  val Modulo = "I LET HIM GO"
  val LambdaDef = "CALL ME SNAKE"
  val FunctionRef = "THE NAME'S PLISSKEN"
  val AsyncStart = "COVER ME"
  val AsyncEnd = "MISSION COMPLETE"
  val Await = "HOLD THE LINE"
  val BitwiseAnd = "WINNERS GO HOME AND DATE THE PROM QUEEN"
  val BitwiseOr = "DEAD OR ALIVE YOU'RE COMING WITH ME"
  val BitwiseXor = "FRIEND OR FOE"
  val LeftShift = "MOVE IT"
  val RightShift = "FALL BACK"
  val ClassStart = "MY NAME IS MAXIMUS"
  val ClassEnd = "STRENGTH AND HONOR"
  val Inherits = "LIKE FATHER LIKE SON"
  val PublicField = "OPEN TO THE PUBLIC"
  val PrivateField = "THAT'S CLASSIFIED"
  val ConstructorStart = "IT'S ALIVE"
  val ConstructorEnd = "BIRTH COMPLETE"
  val NewInstance = "WELCOME TO EARTH"
  val As = "AS"
  val This = "LOOK AT ME"
  val InstanceMethodStart = "COMMANDER IN CHIEF"
  val InstanceMethodEnd = "DISMISSED SOLDIER"
  val Increment = "ONE MORE TIME"
  val Decrement = "COUNTDOWN"
  val ForStart = "LET'S ROCK"
  val ForFrom = "FROM"
  val ForTo = "TO"
  val ForEnd = "GAME OVER MAN GAME OVER"
  val Break = "GET OUT"
  val Continue = "KEEP MOVING"
  val SwitchStart = "CHOOSE YOUR DESTINY"
  val SwitchCase = "WHAT IF I TOLD YOU"
  val SwitchDefault = "SAME OLD SAME OLD"
  val SwitchEnd = "FINISH HIM"
  val MathAbs = "NO MORE HALF MEASURES"
  val MathSqrt = "GET TO THE ROOT OF"
  val MathMax = "MAXIMUM EFFORT OF"
  val MathMin = "MINIMAL CASUALTIES OF"
  val MathPow = "UNLIMITED POWER OF"
  val MathRandom = "GO AHEAD MAKE MY DAY"
  val MathFloor = "HIT THE FLOOR"
  val MathCeil = "THROUGH THE ROOF"
  val MathRound = "ROUND THEM UP"
  val TimeNow = "WHAT TIME IS IT"
  val Sleep = "CHILL OUT FOR"
  val FileRead = "WHAT'S IN THE BOX"
  val FileWrite = "WRITE THAT DOWN"
  val FileWriteTo = "TO"
  val FileExists = "HONEY I'M HOME"
  val FileDelete = "SEAL THE EXITS"
  val Assert = "I AM THE LAW"
  val TryStart = "LET'S SEE WHAT YOU'VE GOT"
  val Throw = "WELCOME TO THE PARTY PAL"
  val Catch = "GOTCHA"
  val Finally = "CLEAN UP ON AISLE FIVE"
  val TryEnd = "THAT'S A WRAP"
  val DeclareArray = "I AIN'T GOT TIME TO BLEED"
  val DeclareFloatArray = "LOCK AND LOAD"
  val Split = "DIVIDE AND CONQUER"
  val ArraySize = "UGLY MOTHERFUCKERS"
  val ArrayWith = "WITH"
  val ArrayAccess = "GET IN LINE"
  val ArrayAt = "AT"
  val ArrayLength = "HOW MANY OF THEM"
  val DeclareFloat = "NOW I HAVE A MACHINE GUN"
  val InitFloat = "HO HO HO"
  val DeclareString = "I HAVE COME HERE TO CHEW BUBBLEGUM"
  val StringAssign = "AND KICK ASS"
  val EmptyString = "AND I'M ALL OUT OF BUBBLEGUM"
  val StrUpper = "SAY IT LOUDER"
  val StrLower = "KEEP YOUR VOICE DOWN"
  val StrTrim = "CUT THE FAT FROM"
  val StrSubstring = "GIVE ME A PIECE OF"
  val StrSubFrom = "FROM"
  val StrSubTo = "TO"
  val StrLength = "HOW LONG IS THIS THING"
  val StrContains = "YOU TALKING TO ME ABOUT"
  val StrIndexOf = "WHERE IS IT IN"
  val NumToString = "SPELL IT OUT"
  val ParseInt = "DO THE MATH"
  val StrReplace = "GET A NEW ONE"
  val StrStartsWith = "FIRST BLOOD"
  val StrEndsWith = "LAST MAN STANDING"
  val StrCharAt = "SHOW ME THE ONE AT"
  val StrReverse = "PUT IT IN REVERSE"

  val SingleLineComment = "I'M BATMAN"
  val BlockCommentStart = "GATHER ROUND"
  val BlockCommentEnd = "DISMISSED"

  // A single-line comment runs to (but not including) the end of the line.
  def LineComment: Rule0 = rule { SingleLineComment ~ zeroOrMore(!anyOf("\n") ~ ANY) }
  // A block comment spans everything between the start and end markers.
  def BlockComment: Rule0 = rule { BlockCommentStart ~ zeroOrMore(!BlockCommentEnd ~ ANY) ~ BlockCommentEnd }

  val EOL = rule {
    zeroOrMore("\t" | "\r" | " ") ~ optional(BlockComment | LineComment) ~ "\n" ~
      zeroOrMore("\t" | "\r" | " " | "\n" | BlockComment | LineComment)
  }
  val WhiteSpace = oneOrMore(" " | "\t")

  def Root: Rule1[RootNode] = rule {
    optional(EOL) ~ zeroOrMore(ClassDefinition) ~ oneOrMore(AbstractMethod) ~ EOI ~~> RootNode
  }

  def ClassDefinition: Rule1[ClassDefNode] = rule {
    ClassStart ~ WhiteSpace ~ VariableName ~> (v => v) ~
      optional(WhiteSpace ~ Inherits ~ WhiteSpace ~ VariableName ~> (v => v)) ~ EOL ~
      zeroOrMore(FieldDeclaration) ~
      optional(ConstructorDefinition) ~
      zeroOrMore(InstanceMethodDefinition) ~
      ClassEnd ~ optional(EOL) ~~> ClassDefNode
  }

  def FieldDeclaration: Rule1[FieldNode] = rule {
    PublicField ~ WhiteSpace ~ VariableName ~> (v => FieldNode(v, true)) ~ EOL |
      PrivateField ~ WhiteSpace ~ VariableName ~> (v => FieldNode(v, false)) ~ EOL
  }

  def ConstructorDefinition: Rule1[List[StatementNode]] = rule {
    ConstructorStart ~ EOL ~ zeroOrMore(Statement) ~ ConstructorEnd ~ EOL
  }

  def InstanceMethodDefinition: Rule1[InstanceMethodNode] = rule {
    InstanceMethodStart ~ WhiteSpace ~ VariableName ~> (v => v) ~ EOL ~
      zeroOrMore(MethodArguments ~ WhiteSpace ~ Variable ~ EOL) ~
      (NonVoidMethod | "") ~> ((m: String) => m == NonVoidMethod) ~ optional(EOL) ~
      zeroOrMore(Statement) ~ InstanceMethodEnd ~ EOL ~~> InstanceMethodNode
  }

  def AbstractMethod: Rule1[AbstractMethodNode] = rule {
    (MainMethod | Method | LambdaDefinition) ~ optional(EOL)
  }

  def LambdaDefinition: Rule1[AbstractMethodNode] = rule {
    LambdaDef ~ WhiteSpace ~ VariableName ~> (v => v) ~ WhiteSpace ~
      LambdaParams ~ WhiteSpace ~ "=>" ~ WhiteSpace ~ LambdaBody ~ EOL ~~> LambdaMethodNode
  }

  def LambdaParams: Rule1[List[VariableNode]] = rule {
    "(" ~ zeroOrMore(optional(WhiteSpace) ~ Variable) ~ optional(WhiteSpace) ~ ")"
  }

  def LambdaBody: Rule1[AstNode] = rule {
    Operand ~ WhiteSpace ~ PlusOperator ~ WhiteSpace ~ Operand ~~> PlusExpressionNode |
      Operand ~ WhiteSpace ~ MinusOperator ~ WhiteSpace ~ Operand ~~> MinusExpressionNode |
      Operand ~ WhiteSpace ~ MultiplicationOperator ~ WhiteSpace ~ Operand ~~> MultiplicationExpressionNode |
      Operand ~ WhiteSpace ~ DivisionOperator ~ WhiteSpace ~ Operand ~~> DivisionExpressionNode |
      Operand ~ WhiteSpace ~ Modulo ~ WhiteSpace ~ Operand ~~> ModuloExpressionNode |
      Operand
  }

  def MainMethod: Rule1[AbstractMethodNode] = rule {
    BeginMain ~ EOL ~ zeroOrMore(Statement) ~ EndMain ~~> MainMethodNode
  }

  def Method: Rule1[AbstractMethodNode] = rule {
    DeclareMethod ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~
      zeroOrMore((MethodArguments ~ WhiteSpace ~ Variable ~ EOL)) ~
      (NonVoidMethod | "") ~> ((m: String) => m == NonVoidMethod) ~ optional(EOL) ~
      zeroOrMore(Statement) ~ EndMethodDeclaration ~~> MethodNode
  }

  def Statement: Rule1[StatementNode] = rule {
    DeclareIntStatement | PrintStatement |
      NewInstanceStatement | ThisFieldAssignStatement | FieldAssignStatement |
      InstanceMethodCallStatement |
      AssignVariableStatement | ConditionStatement |
      WhileStatement | CallMethodStatement | ReturnStatement | CallReadMethodStatement |
      IncrementStatement | DecrementStatement | ForLoopStatement |
      BreakStatement | ContinueStatement | SwitchStatement |
      StringDeclareStatement | FloatDeclareStatement |
      ArrayDeclareStatement | FloatArrayDeclareStatement | SplitDeclareStatement | ArrayAssignStatement |
      TryStatement | ThrowStatement | AssertStatement | SleepStatement |
      WriteFileStatement | DeleteFileStatement |
      AsyncBlockStatement | AwaitStatement
  }

  def AsyncBlockStatement: Rule1[StatementNode] = rule {
    AsyncStart ~ WhiteSpace ~ VariableName ~> (v => v) ~ EOL ~
      zeroOrMore(Statement) ~ AsyncEnd ~ EOL ~~> AsyncBlockNode
  }

  def AwaitStatement: Rule1[StatementNode] = rule {
    Await ~ WhiteSpace ~ VariableName ~> (v => v) ~ EOL ~~> AwaitNode
  }

  def WriteFileStatement: Rule1[StatementNode] = rule {
    FileWrite ~ WhiteSpace ~ StringOperand ~ WhiteSpace ~ FileWriteTo ~ WhiteSpace ~ StringOperand ~ EOL ~~> WriteFileNode
  }

  def DeleteFileStatement: Rule1[StatementNode] = rule {
    FileDelete ~ WhiteSpace ~ StringOperand ~ EOL ~~> DeleteFileNode
  }

  def SleepStatement: Rule1[StatementNode] = rule {
    Sleep ~ WhiteSpace ~ Operand ~ EOL ~~> SleepNode
  }

  def AssertStatement: Rule1[StatementNode] = rule {
    Assert ~ WhiteSpace ~ Operand ~ optional(WhiteSpace ~ "\"" ~ String ~ "\"") ~ EOL ~~> AssertNode
  }

  def TryStatement: Rule1[StatementNode] = rule {
    TryStart ~ EOL ~ zeroOrMore(Statement) ~
      Catch ~ WhiteSpace ~ VariableName ~> (v => v) ~ EOL ~ zeroOrMore(Statement) ~
      optional(Finally ~ EOL ~ zeroOrMore(Statement)) ~
      TryEnd ~ EOL ~~> TryNode
  }

  def ThrowStatement: Rule1[StatementNode] = rule {
    Throw ~ WhiteSpace ~ StringOperand ~ EOL ~~> ThrowNode
  }

  def ArrayDeclareStatement: Rule1[StatementNode] = rule {
    DeclareArray ~ WhiteSpace ~ VariableName ~> (v => v) ~ WhiteSpace ~
      ArrayWith ~ WhiteSpace ~ Operand ~ WhiteSpace ~ ArraySize ~ EOL ~~> ArrayDeclareNode
  }

  def FloatArrayDeclareStatement: Rule1[StatementNode] = rule {
    DeclareFloatArray ~ WhiteSpace ~ VariableName ~> (v => v) ~ WhiteSpace ~
      ArrayWith ~ WhiteSpace ~ Operand ~ WhiteSpace ~ ArraySize ~ EOL ~~> FloatArrayDeclareNode
  }

  def SplitDeclareStatement: Rule1[StatementNode] = rule {
    Split ~ WhiteSpace ~ VariableName ~> (v => v) ~ WhiteSpace ~
      StringOperand ~ WhiteSpace ~ StringOperand ~ EOL ~~> SplitDeclareNode
  }

  def ArrayAssignStatement: Rule1[StatementNode] = rule {
    ArrayAccess ~ WhiteSpace ~ VariableName ~> (v => v) ~ WhiteSpace ~
      ArrayAt ~ WhiteSpace ~ Operand ~ EOL ~
      Expression ~ EndAssignVariable ~ EOL ~~> ArrayAssignNode
  }

  def FloatDeclareStatement: Rule1[StatementNode] = rule {
    DeclareFloat ~ WhiteSpace ~ VariableName ~> (v => v) ~ EOL ~
      InitFloat ~ WhiteSpace ~ FloatLiteral ~ EOL ~~> FloatDeclareNode
  }

  def FloatLiteral: Rule1[FloatNode] = rule {
    group(optional("-") ~ oneOrMore("0" - "9") ~ "." ~ oneOrMore("0" - "9")) ~> (s => FloatNode(s.toFloat))
  }

  def StringDeclareStatement: Rule1[StatementNode] = rule {
    DeclareString ~ WhiteSpace ~ VariableName ~> (v => v) ~ EOL ~
      oneOrMore(StringAssign ~ WhiteSpace ~ StringOperand ~ EOL) ~~> StringDeclareNode
  }

  def StringOperand: Rule1[OperandNode] = rule {
    StringFunction |
      ArrayAccessOperand |
      (FileRead ~ WhiteSpace ~ StringOperand ~~> ReadFileNode) |
      "\"" ~ String ~ "\"" |
      EmptyString ~ push(StringNode("")) |
      Variable
  }

  def StringFunction: Rule1[OperandNode] = rule {
    NumToString ~ WhiteSpace ~ Operand ~~> NumToStringNode |
      StrReplace ~ WhiteSpace ~ StringOperand ~ WhiteSpace ~ StringOperand ~ WhiteSpace ~ StringOperand ~~> ReplaceNode |
      StrCharAt ~ WhiteSpace ~ StringOperand ~ WhiteSpace ~ Operand ~~> CharAtNode |
      StrReverse ~ WhiteSpace ~ StringOperand ~~> ReverseNode |
      StrUpper ~ WhiteSpace ~ StringOperand ~~> UpperNode |
      StrLower ~ WhiteSpace ~ StringOperand ~~> LowerNode |
      StrTrim ~ WhiteSpace ~ StringOperand ~~> TrimNode |
      StrSubstring ~ WhiteSpace ~ StringOperand ~ WhiteSpace ~
        StrSubFrom ~ WhiteSpace ~ Operand ~ WhiteSpace ~ StrSubTo ~ WhiteSpace ~ Operand ~~> SubstringNode
  }

  def SwitchStatement: Rule1[StatementNode] = rule {
    SwitchStart ~ WhiteSpace ~ Operand ~ EOL ~
      zeroOrMore(CaseClauseRule) ~
      optional(DefaultClauseRule) ~
      SwitchEnd ~ EOL ~~> SwitchNode
  }

  def CaseClauseRule: Rule1[CaseClause] = rule {
    SwitchCase ~ WhiteSpace ~ Number ~ EOL ~ zeroOrMore(Statement) ~~> CaseClause
  }

  def DefaultClauseRule: Rule1[List[StatementNode]] = rule {
    SwitchDefault ~ EOL ~ zeroOrMore(Statement) ~~> (stmts => stmts)
  }

  def BreakStatement: Rule1[StatementNode] = rule {
    Break ~ EOL ~ push(BreakNode())
  }

  def ContinueStatement: Rule1[StatementNode] = rule {
    Continue ~ EOL ~ push(ContinueNode())
  }

  def ForLoopStatement: Rule1[StatementNode] = rule {
    ForStart ~ WhiteSpace ~ VariableName ~> (v => v) ~ WhiteSpace ~
      ForFrom ~ WhiteSpace ~ Operand ~ WhiteSpace ~
      ForTo ~ WhiteSpace ~ Operand ~ EOL ~
      zeroOrMore(Statement) ~ ForEnd ~ EOL ~~> ForLoopNode
  }

  def IncrementStatement: Rule1[StatementNode] = rule {
    Increment ~ WhiteSpace ~ VariableName ~> (v => v) ~ EOL ~~> IncrementNode
  }

  def DecrementStatement: Rule1[StatementNode] = rule {
    Decrement ~ WhiteSpace ~ VariableName ~> (v => v) ~ EOL ~~> DecrementNode
  }

  def NewInstanceStatement: Rule1[StatementNode] = rule {
    NewInstance ~ WhiteSpace ~ VariableName ~> (v => v) ~ WhiteSpace ~ As ~ WhiteSpace ~ VariableName ~> (v => v) ~ EOL ~~> NewInstanceNode
  }

  def ThisFieldAssignStatement: Rule1[StatementNode] = rule {
    AssignVariable ~ WhiteSpace ~ This ~ "." ~ VariableName ~> (v => v) ~ EOL ~
      Expression ~ EndAssignVariable ~ EOL ~~> ThisFieldAssignNode
  }

  def FieldAssignStatement: Rule1[StatementNode] = rule {
    AssignVariable ~ WhiteSpace ~ VariableName ~> (v => v) ~ "." ~ VariableName ~> (v => v) ~ EOL ~
      Expression ~ EndAssignVariable ~ EOL ~~> FieldAssignNode
  }

  def InstanceMethodCallStatement: Rule1[StatementNode] = rule {
    (AssignVariableFromMethodCall ~ WhiteSpace ~ VariableName ~> (v => v) ~ EOL | "" ~> (v => v)) ~
      CallMethod ~ WhiteSpace ~ VariableName ~> (v => v) ~ "." ~ VariableName ~> (v => v) ~
      zeroOrMore(WhiteSpace ~ Operand) ~ EOL ~~> InstanceMethodCallNode
  }

  def CallMethodStatement: Rule1[StatementNode] = rule {
    (AssignVariableFromMethodCall ~ WhiteSpace ~ VariableName ~> (v => v) ~ EOL | "" ~> (v => v)) ~
      CallMethod ~ WhiteSpace ~ VariableName ~> (v => v) ~
      zeroOrMore(WhiteSpace ~ Operand) ~ EOL ~~> CallMethodNode
  }

  def CallReadMethodStatement: Rule1[StatementNode] = rule {
    (AssignVariableFromMethodCall ~ WhiteSpace ~ VariableName ~> (v => v) ~ EOL | "" ~> (v => v)) ~
      CallMethod ~ EOL ~ Read ~ EOL ~~> CallReadMethodNode
  }

  def ConditionStatement: Rule1[ConditionNode] = rule {
    If ~ WhiteSpace ~ Operand ~ EOL ~ zeroOrMore(Statement) ~
      (Else ~ EOL ~ zeroOrMore(Statement) ~~> ConditionNode
        | zeroOrMore(Statement) ~~> ConditionNode) ~ EndIf ~ EOL

  }

  def WhileStatement: Rule1[WhileNode] = rule {
    While ~ WhiteSpace ~ Operand ~ EOL ~ zeroOrMore(Statement) ~ EndWhile ~ EOL ~~> WhileNode
  }

  def PrintStatement: Rule1[PrintNode] = rule {
    Print ~ WhiteSpace ~ (StringFunction ~~> PrintNode | Operand ~~> PrintNode | "\"" ~ String ~ "\"" ~~> PrintNode) ~ EOL
  }

  def DeclareIntStatement: Rule1[DeclareIntNode] = rule {
    DeclareInt ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~ SetInitialValue ~ WhiteSpace ~ Operand ~~> DeclareIntNode ~ EOL
  }

  def AssignVariableStatement: Rule1[AssignVariableNode] = rule {
    AssignVariable ~ WhiteSpace ~ VariableName ~> (s => s) ~ EOL ~ Expression ~ EndAssignVariable ~ EOL ~~> AssignVariableNode
  }

  def ReturnStatement: Rule1[StatementNode] = rule {
    Return ~ ((WhiteSpace ~ Operand ~~> (o => ReturnNode(Some(o)))) | "" ~> (s => ReturnNode(None))) ~ EOL
  }

  def Operand: Rule1[OperandNode] = rule {
    FloatLiteral | Number | ArrayAccessOperand | ArrayLengthOperand | MathOperand | StringIntFunction |
      (TimeNow ~ push(TimeNode())) | (FileExists ~ WhiteSpace ~ StringOperand ~~> FileExistsNode) |
      ThisFieldAccessOperand | FieldAccessOperand |
      (FunctionRef ~ WhiteSpace ~ VariableName ~> (v => v) ~~> FunctionRefNode) |
      Variable | Boolean
  }

  def ThisFieldAccessOperand: Rule1[OperandNode] = rule {
    This ~ "." ~ VariableName ~> (v => v) ~~> ThisFieldAccessNode
  }

  def FieldAccessOperand: Rule1[OperandNode] = rule {
    VariableName ~> (v => v) ~ "." ~ VariableName ~> (v => v) ~~> FieldAccessNode
  }

  def StringIntFunction: Rule1[OperandNode] = rule {
    ParseInt ~ WhiteSpace ~ StringOperand ~~> ParseIntNode |
      StrStartsWith ~ WhiteSpace ~ StringOperand ~ WhiteSpace ~ StringOperand ~~> StartsWithNode |
      StrEndsWith ~ WhiteSpace ~ StringOperand ~ WhiteSpace ~ StringOperand ~~> EndsWithNode |
      StrLength ~ WhiteSpace ~ StringOperand ~~> LengthNode |
      StrContains ~ WhiteSpace ~ StringOperand ~ WhiteSpace ~ StringOperand ~~> ContainsNode |
      StrIndexOf ~ WhiteSpace ~ StringOperand ~ WhiteSpace ~ StringOperand ~~> IndexOfNode
  }

  def MathOperand: Rule1[OperandNode] = rule {
    MathAbs ~ WhiteSpace ~ Operand ~~> AbsNode |
      MathSqrt ~ WhiteSpace ~ Operand ~~> SqrtNode |
      MathMax ~ WhiteSpace ~ Operand ~ WhiteSpace ~ Operand ~~> MaxNode |
      MathMin ~ WhiteSpace ~ Operand ~ WhiteSpace ~ Operand ~~> MinNode |
      MathPow ~ WhiteSpace ~ Operand ~ WhiteSpace ~ Operand ~~> PowNode |
      MathRandom ~ WhiteSpace ~ Operand ~~> RandomNode |
      MathFloor ~ WhiteSpace ~ Operand ~~> FloorNode |
      MathCeil ~ WhiteSpace ~ Operand ~~> CeilNode |
      MathRound ~ WhiteSpace ~ Operand ~~> RoundNode
  }

  def ArrayAccessOperand: Rule1[OperandNode] = rule {
    ArrayAccess ~ WhiteSpace ~ VariableName ~> (v => v) ~ WhiteSpace ~ ArrayAt ~ WhiteSpace ~ Operand ~~> ArrayAccessNode
  }

  def ArrayLengthOperand: Rule1[OperandNode] = rule {
    ArrayLength ~ WhiteSpace ~ VariableName ~> (v => v) ~~> ArrayLengthNode
  }

  def Expression: Rule1[AstNode] = rule {
    SetValueExpression ~
      (zeroOrMore(ArithmeticOperation | LogicalOperation | BitwiseOperation | UnaryOperation))
  }

  def BitwiseOperation: ReductionRule1[AstNode, AstNode] = rule {
    BitwiseAnd ~ WhiteSpace ~ Operand ~ EOL ~~> BitwiseAndNode |
      BitwiseOr ~ WhiteSpace ~ Operand ~ EOL ~~> BitwiseOrNode |
      BitwiseXor ~ WhiteSpace ~ Operand ~ EOL ~~> BitwiseXorNode |
      LeftShift ~ WhiteSpace ~ Operand ~ EOL ~~> LeftShiftNode |
      RightShift ~ WhiteSpace ~ Operand ~ EOL ~~> RightShiftNode
  }

  def UnaryOperation: ReductionRule1[AstNode, AstNode] = rule {
    Not ~ EOL ~~> NotNode
  }

  def LogicalOperation: ReductionRule1[AstNode, AstNode] = rule {
    Or ~ WhiteSpace ~ Operand ~ EOL ~~> OrNode |
      And ~ WhiteSpace ~ Operand ~ EOL ~~> AndNode |
      EqualTo ~ WhiteSpace ~ Operand ~ EOL ~~> EqualToNode |
      NotEqual ~ WhiteSpace ~ Operand ~ EOL ~~> NotEqualNode |
      GreaterThan ~ WhiteSpace ~ Operand ~ EOL ~~> GreaterThanNode |
      LessThan ~ WhiteSpace ~ Operand ~ EOL ~~> LessThanNode |
      GreaterOrEqual ~ WhiteSpace ~ Operand ~ EOL ~~> GreaterOrEqualNode |
      LessOrEqual ~ WhiteSpace ~ Operand ~ EOL ~~> LessOrEqualNode

  }

  def RelationalExpression: ReductionRule1[AstNode, AstNode] = {
    EqualToExpression ~~> EqualToNode |
      GreaterThanExpression ~~> GreaterThanNode
  }


  def EqualToExpression: Rule1[OperandNode] = {
    EqualTo ~ WhiteSpace ~ Operand ~ EOL
  }

  def GreaterThanExpression: Rule1[OperandNode] = {
    GreaterThan ~ WhiteSpace ~ Operand ~ EOL
  }

  def ArithmeticOperation: ReductionRule1[AstNode, AstNode] = rule {
    PlusExpression ~~> PlusExpressionNode |
      MinusExpression ~~> MinusExpressionNode |
      MultiplicationExpression ~~> MultiplicationExpressionNode |
      DivisionExpression ~~> DivisionExpressionNode |
      ModuloExpression ~~> ModuloExpressionNode
  }

  def SetValueExpression: Rule1[OperandNode] = rule {
    SetValue ~ WhiteSpace ~ Operand ~ EOL
  }


  def PlusExpression: Rule1[AstNode] = rule {
    PlusOperator ~ WhiteSpace ~ Operand ~ EOL
  }

  def MinusExpression: Rule1[AstNode] = rule {
    MinusOperator ~ WhiteSpace ~ Operand ~ EOL
  }

  def MultiplicationExpression: Rule1[AstNode] = rule {
    MultiplicationOperator ~ WhiteSpace ~ Operand ~ EOL
  }

  def DivisionExpression: Rule1[AstNode] = rule {
    DivisionOperator ~ WhiteSpace ~ Operand ~ EOL
  }

  def ModuloExpression: Rule1[AstNode] = rule {
    Modulo ~ WhiteSpace ~ Operand ~ EOL
  }

  def Variable: Rule1[VariableNode] = rule {
    VariableName ~> VariableNode
  }

  def VariableName: Rule0 = rule {
    rule("A" - "Z" | "a" - "z") ~ zeroOrMore("A" - "Z" | "a" - "z" | "0" - "9")
  }

  def Number: Rule1[NumberNode] = rule {
    oneOrMore("0" - "9") ~> ((matched: String) => NumberNode(matched.toInt)) |
      "-" ~ oneOrMore("0" - "9") ~> ((matched: String) => NumberNode(-matched.toInt))
  }

  def Boolean: Rule1[NumberNode] = rule {
    "@" ~ True ~> (_ => NumberNode(1)) |
      "@" ~ False ~> (_ => NumberNode(0))
  }

  def String: Rule1[StringNode] = rule {
    zeroOrMore(rule {
      !anyOf("\"\\") ~ ANY
    }) ~> StringNode
  }

  def parse(expression: String): RootNode = {
    val parsingResult = ReportingParseRunner(Root).run(expression)
    parsingResult.result match {
      case Some(root) => root
      case None => throw new ParsingException(ParseError + ":\n" +
        ErrorUtils.printParseErrors(parsingResult))
    }
  }

}