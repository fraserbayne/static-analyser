

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MiddleMan {

	public static void main(File file) throws Exception {

		FileInputStream fileInputStream = new FileInputStream(file);
		CompilationUnit cu;
		try {
			cu = StaticJavaParser.parse(fileInputStream);
		} finally {
			fileInputStream.close();
		}
		new MiddleManVisitor().visit(cu, null);

	}

	private static class MiddleManVisitor extends VoidVisitorAdapter {
		@Override
		public void visit(ClassOrInterfaceDeclaration n, Object arg) {
			List<Node> list = new ArrayList<Node>();
			for (MethodDeclaration method : n.getMethods()) {

				method.accept(new MiddleManMethodVisitor(), list);
				if (list.size() > 0) {
					System.out.println("[Middle man] - Class: " + n.getNameAsString());
					break;
				}

			}
			super.visit(n, arg);
		}
	}

	private static class MiddleManMethodVisitor extends VoidVisitorAdapter<List<Node>> {

		@Override
		public void visit(MethodDeclaration n, List<Node> arg) {
			// if contains block with 1 statement
			if (n.findAll(Statement.class).size() == 2) {

				n.accept(new MiddleManReturnStmtVisitor(), arg);
			}
		}

	}

	private static class MiddleManReturnStmtVisitor extends VoidVisitorAdapter<List<Node>> {

		// is return statement
		@Override
		public void visit(ReturnStmt n, List<Node> arg) {
			n.accept(new MiddleManMethodCallExprVisitor(), arg);
		}

	}

	private static class MiddleManMethodCallExprVisitor extends VoidVisitorAdapter<List<Node>> {

		@Override
		public void visit(MethodCallExpr n, List<Node> arg) {

			arg.add(n);
		}

		public void visit(FieldAccessExpr n, List<Node> arg) {

			arg.add(n);
		}
	}

}