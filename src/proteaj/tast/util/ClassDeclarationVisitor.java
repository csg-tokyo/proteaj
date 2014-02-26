package proteaj.tast.util;

import proteaj.tast.*;

public abstract class ClassDeclarationVisitor<T> {
  protected abstract T visit (ClassDeclaration clazz);
  protected abstract T visit (MethodDeclaration method);
  protected abstract T visit (ConstructorDeclaration constructor);
  protected abstract T visit (FieldDeclaration field);
  protected abstract T visit (DefaultValueDefinition defaultValue);
}
