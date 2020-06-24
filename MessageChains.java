import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MessageChains {

	private static final int CHAIN_LIMIT = 2;

	public static void main(File file) throws Exception {

		FileInputStream fileInputStream = new FileInputStream(file);
		CompilationUnit cu;
		try {
			cu = StaticJavaParser.parse(fileInputStream);
		} finally {
			fileInputStream.close();
		}

		new MessageChainsVisitor().visit(cu, null);
	}

	private static class MessageChainsVisitor extends VoidVisitorAdapter {

		@Override
		public void visit(MethodCallExpr n, Object arg) {

			List<MethodCallExpr> methodCalls = new ArrayList<MethodCallExpr>();
			n.accept(new MessageChainCallsVisitor(), methodCalls);
			int chainLength = methodCalls.size();
			if (chainLength > CHAIN_LIMIT) {
				System.out.println("[Message Chain] Line: " + n.getBegin().get().line + " - Chain length: "
						+ chainLength + " - " + n);

			}

		}

	}

	private static class MessageChainCallsVisitor extends VoidVisitorAdapter<List<MethodCallExpr>> {
		@Override
		public void visit(MethodCallExpr n, List<MethodCallExpr> methodCalls) {
			methodCalls.add(n);

			Optional<Expression> scope = n.getScope();
			if (scope.isPresent()) {
				scope.get().accept(new MessageChainCallsVisitor(), methodCalls);
			}
		}

	}

}