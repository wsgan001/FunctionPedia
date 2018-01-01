package cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression.IRAbstractVariable;

public interface IRAbstractStatement {
	BasicCFGRegularBlock buildCFG(BasicCFGRegularBlock block);

	abstract class ExpressionFilter {
		private Stream.Builder<IRExpression> builder = Stream.builder();
		private List<Stream<IRExpression>> streams = new ArrayList<>();

		public abstract boolean filter(IRExpression expression);

		public final ExpressionFilter add(IRExpression expression) {
			if (filter(expression)) builder.add(expression);
			return this;
		}

		public final ExpressionFilter addAll(IRExpression[] expressions){
			if (expressions == null) return this;
			streams.add(Stream.of(expressions).filter(this::filter));
			return this;
		}

		public final Stream<IRExpression> build(){
			Stream<IRExpression> result = builder.build();
			return streams.stream().reduce(result, Stream::concat);
		}
	}

	class VariableFilter extends ExpressionFilter {
		public boolean filter(IRExpression expression) {
			return expression != null && expression instanceof IRAbstractVariable;
		}
	}

	class PreDefinedFilter extends ExpressionFilter {
		public boolean filter(IRExpression expression) {
			return expression != null && !(expression instanceof IRAbstractVariable);
		}
	}
}
