import java.io.File;
import java.io.FileInputStream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class LargeClass {

	private static final int STATEMENT_LIMIT = 100;
	private static final int LINE_LIMIT = 100;

	public static void main(File file) throws Exception {

		FileInputStream fileInputStream = new FileInputStream(file);
		CompilationUnit cu;
		try {
			cu = StaticJavaParser.parse(fileInputStream);
		} finally {
			fileInputStream.close();
		}

		new LargeClassVisitor().visit(cu, null);

	}

	private static class LargeClassVisitor extends VoidVisitorAdapter {

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Object arg) {

			// Statements
			int statements = n.findAll(Statement.class).size() - n.findAll(BlockStmt.class).size();
			if (statements > STATEMENT_LIMIT) {
				System.out.println("[Large Class]" + n.getName() + " - statements: " + statements);
			}

			// Lines
			int lines = (n.getEnd().get().line - 1) - (n.getBegin().get().line + 1);
			if (lines > LINE_LIMIT) {
				System.out.println("[Large Class]" + n.getName() + " - lines: " + lines);
			}

		}
	}

}