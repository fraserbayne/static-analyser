import java.io.File;
import java.io.FileInputStream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class LongParameterList {
	private static final int PARAMETER_LIMIT = 5;

	public static void main(File file) throws Exception {

		FileInputStream fileInputStream = new FileInputStream(file);
		CompilationUnit cu;
		try {
			cu = StaticJavaParser.parse(fileInputStream);
		} finally {
			fileInputStream.close();
		}

		new LongParameterListVisitor().visit(cu, null);

	}

	private static class LongParameterListVisitor extends VoidVisitorAdapter {

		@Override
		public void visit(MethodDeclaration n, Object arg) {

			int parameters = n.getParameters().size();
			if (parameters > PARAMETER_LIMIT) {
				System.out.println(
						"[Long Parameter List] - Method: " + n.getNameAsString() + " - parameters: " + parameters);
			}

		}
	}
}