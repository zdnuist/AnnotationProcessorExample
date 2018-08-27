package me.zdnuist.annotation.module_processor;

import android.support.annotation.Keep;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import me.zdnuist.annotation.module_annotation.FindView;

/**
 * @createtime: 2018/8/24 on 16:34
 * @desc:
 * @author: zd
 */
@AutoService(Processor.class)
public class FindViewProcessor extends AbstractProcessor {

  Messager mMessager;
  Elements elementUtils;
  Filer mFiler;

  private Map<TypeElement , List<Element>> elementListMap = new HashMap<>();

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);

    mMessager = processingEnvironment.getMessager();
    elementUtils = processingEnvironment.getElementUtils();
    mFiler = processingEnvironment.getFiler();
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Collections.singleton(FindView.class.getCanonicalName());
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {


    Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(FindView.class);

    if(elements == null || elements.size() == 0){
      return false;
    }

    for(Element element : elements){
      println(this.hashCode()+"");
      println(element.getSimpleName().toString());
      println(element.getEnclosingElement().toString());

      List<Element> list = elementListMap.get((TypeElement) element.getEnclosingElement());
      if(list == null){
        list = new ArrayList<>();
      }

      list.add(element);

      elementListMap.put((TypeElement) element.getEnclosingElement() , list);
    }

    Iterator<Entry<TypeElement , List<Element>>> iterator = elementListMap.entrySet().iterator();
    while (iterator.hasNext()){
      Entry<TypeElement, List<Element>> entry = iterator.next();
      TypeElement key = entry.getKey();
      List<Element> elementList = entry.getValue();

      try {
        generateFindViewCode(elementList, key);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return false;
  }

  private void generateFindViewCode(List<Element> elementList ,TypeElement typeElement) throws IOException{
    println(typeElement.toString());
    TypeName actTypeName = ParameterizedTypeName.get(typeElement.asType());
    ParameterSpec actParameterSpec = ParameterSpec.builder(actTypeName, "target").build();
    FieldSpec fieldSpec = FieldSpec.builder(actTypeName, "target" , Modifier.PRIVATE).build();

    TypeName viewTypeName = ParameterizedTypeName.get(elementUtils.getTypeElement("android.view.View").asType());
    ParameterSpec viewParameterSpec = ParameterSpec.builder(viewTypeName, "source").build();

    boolean isActivity = isSubtypeOfType(typeElement.asType(),ACTIVITY_TYPE);

    MethodSpec.Builder methodSpecBuilder1 = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
        .addParameter(actParameterSpec);

    if(isActivity) {
      methodSpecBuilder1.addStatement("this(target, target.getWindow().getDecorView())");
    }

    MethodSpec methodSpec = methodSpecBuilder1.build();


    MethodSpec.Builder methodSpecBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
        .addParameter(actParameterSpec).addParameter(viewParameterSpec)
        .addStatement("this.$N = $N" , "target" , "target")
        ;

    for(Element e : elementList){
      FindView findView = e.getAnnotation(FindView.class);
      methodSpecBuilder.addStatement("target.$N = $T.findRequiredViewAsType(source, "+ findView.value() +", \"field '"+ e.getSimpleName().toString()+"' \", $T.class)" ,
          e.getSimpleName(),
          ClassName.get(elementUtils.getTypeElement("me.zdnuist.annotation.utils.FindViewUtil"))
          ,ClassName.get(e.asType())
      );
    }


    MethodSpec methodSpec2 = methodSpecBuilder.build();



    String genName = typeElement.getQualifiedName().toString() + "_FindView";

    String className = genName.substring(genName.lastIndexOf(".")+1);
    TypeSpec typeSpec = TypeSpec.classBuilder(className).addMethod(methodSpec)
        .addAnnotation(ClassName.get(Keep.class))
        .addMethod(methodSpec2)
        .addField(fieldSpec).addModifiers(Modifier.PUBLIC).build();

    JavaFile.builder(genName.substring(0, genName.lastIndexOf(".")),typeSpec)
        .build().writeTo(mFiler);



  }

  private void println(String msg){
    mMessager.printMessage(Kind.NOTE, msg);
  }

  static final String ACTIVITY_TYPE = "android.app.Activity";

  static boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
    if (isTypeEqual(typeMirror, otherType)) {
      return true;
    }
    if (typeMirror.getKind() != TypeKind.DECLARED) {
      return false;
    }
    DeclaredType declaredType = (DeclaredType) typeMirror;
    List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
    if (typeArguments.size() > 0) {
      StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
      typeString.append('<');
      for (int i = 0; i < typeArguments.size(); i++) {
        if (i > 0) {
          typeString.append(',');
        }
        typeString.append('?');
      }
      typeString.append('>');
      if (typeString.toString().equals(otherType)) {
        return true;
      }
    }
    Element element = declaredType.asElement();
    if (!(element instanceof TypeElement)) {
      return false;
    }
    TypeElement typeElement = (TypeElement) element;
    TypeMirror superType = typeElement.getSuperclass();
    if (isSubtypeOfType(superType, otherType)) {
      return true;
    }
    for (TypeMirror interfaceType : typeElement.getInterfaces()) {
      if (isSubtypeOfType(interfaceType, otherType)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isTypeEqual(TypeMirror typeMirror, String otherType) {
    return otherType.equals(typeMirror.toString());
  }
}
