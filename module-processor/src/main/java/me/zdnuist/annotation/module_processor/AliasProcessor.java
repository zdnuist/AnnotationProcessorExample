package me.zdnuist.annotation.module_processor;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;
import me.zdnuist.annotation.module_annotation.Alias;

@AutoService(Processor.class)
public class AliasProcessor extends AbstractProcessor{


  Messager messager;
  Filer filer;
  Elements elements;
  Types types;


  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);

    messager = processingEnvironment.getMessager();
    filer = processingEnvironment.getFiler();
    elements = processingEnvironment.getElementUtils();
    types = processingEnvironment.getTypeUtils();

  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Collections.singleton(Alias.class.getCanonicalName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
    print("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Alias.class);

    for(Element element : elements){
      print(element.toString());
      print(element.asType().toString());

      Alias alias = element.getAnnotation(Alias.class);

      try {
        generateCode(element, alias, (TypeElement) element);
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
    }

    return true;
  }

  private void generateCode(Element e, Alias alias, TypeElement clazz) throws IOException{

    JavaFileObject f = filer.createSourceFile(clazz.getQualifiedName() + "$InnerClass");
    print("Create inner class:" + f.toUri());

    Writer w = f.openWriter();
    try {
      String qualifiedName = clazz.getQualifiedName().toString();
      PrintWriter pw = new PrintWriter(w);
      pw.println("package " + qualifiedName.substring(0 , qualifiedName.lastIndexOf(".")) + ";");
      pw.println("\n class " +  clazz.getSimpleName() + "GenAlias { ");
      pw.println("\n protected final void showValue() {");
      pw.println("\n System.out.println(\"value:"+ alias.name() +"\");");
      pw.println("      }");
      pw.println("}");
    }finally {
      w.close();
    }

  }

  private void print(String msg){
    messager.printMessage(Kind.NOTE, msg);
  }
}
