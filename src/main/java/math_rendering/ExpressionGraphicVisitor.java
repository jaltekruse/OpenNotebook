package math_rendering;

public interface ExpressionGraphicVisitor<RET, STATE> {

    public RET visitBinExpression(BinExpressionGraphic ex, STATE state);
    public RET visitUnaryExpression(UnaryExpressionGraphic ex, STATE state);
    public RET visitDecimalGraphic(DecimalGraphic ex, STATE state);
    public RET visitAbsoluteValueGraphic(AbsoluteValueGraphic ex, STATE state);
    public RET visitDivisionGraphic(DivisionGraphic ex, STATE state);
    public RET visitDotMultiplication(DotMultiplication ex, STATE state);
    public RET visitExponentGraphic(ExponentGraphic ex, STATE state);
    public RET visitIdentifierGraphic(IdentifierGraphic ex, STATE state);
    public RET visitImpliedMultGraphic(ImpliedMultGraphic ex, STATE state);
    public RET visitNegationGraphic(NegationGraphic ex, STATE state);
    public RET visitNothingGraphic(NothingGraphic ex, STATE state);
    public RET visitParenGraphic(ParenGraphic ex, STATE state);
    public RET visitRadicalGraphic(RadicalGraphic ex, STATE state);
    public RET visitUnaryPostGraphic(UnaryPostGraphic ex, STATE state);
}
