import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class DataClass {

	public static void main(File file) throws Exception {

		FileInputStream fileInputStream = new FileInputStream(file);
		CompilationUnit cu;
		try {
			cu = StaticJavaParser.parse(fileInputStream);
		} finally {
			fileInputStream.close();
		}

		new DataClassVisitor().visit(cu, null);

	}

	private static class DataClassVisitor extends VoidVisitorAdapter {
		@Override
		public void visit(ClassOrInterfaceDeclaration n, Object arg) {

			if (n.getFields().size() > 0 && n.getMethods().size() == 0) {
				System.out.println("[Data Class] - Class: " + n.getNameAsString() + " - Contains no methods");

			} else {

				List<MethodDeclaration> methods = n.getMethods();

				for (MethodDeclaration methodDeclaration : methods) {
					List<Statement> statements = new ArrayList<Statement>();
					methodDeclaration.accept(new DataClassMethodVisitor(), statements);
					if (statements.size() > 0) {
						super.visit(n, arg);
						return;
					}
				}
				if (methods.size() > 0) {
					System.out.println("[Data Class] - Class: " + n.getNameAsString());

				}
			}

			super.visit(n, arg);

		}

	}

	private static class DataClassMethodVisitor extends VoidVisitorAdapter<List<Statement>> {
		@Override
		public void visit(MethodDeclaration n, List<Statement> statements) {
			statements.clear();
			statements.addAll(n.findAll(Statement.class));
			statements.removeAll(n.findAll(VariableDeclarationExpr.class));
			for (AssignExpr statement : n.findAll(AssignExpr.class)) {
				if ((statement.getTarget() instanceof NameExpr)) {
					statements.remove(statement);
				}
			}

			for (ReturnStmt statement : n.findAll(ReturnStmt.class)) {
				if ((statement.getExpression().get() instanceof NameExpr)) {
					statements.remove(statement);
				}
			}

			statements.removeAll(n.findAll(BlockStmt.class));
		}

	}

}