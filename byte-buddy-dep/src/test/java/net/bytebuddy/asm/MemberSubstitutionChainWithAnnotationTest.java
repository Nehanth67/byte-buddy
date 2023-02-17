package net.bytebuddy.asm;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MemberSubstitutionChainWithAnnotationTest {

    private static final String FOO = "foo", BAR = "bar", QUX = "qux", BAZ = "baz", RUN = "run";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    public void testArgumentToElement() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(ArgumentSample.class)
                .visit(MemberSubstitution.strict()
                        .field(named(FOO))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(ArgumentSample.class.getMethod("element", String.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) QUX));
        assertThat(type.getDeclaredMethod(RUN, String.class).invoke(instance, BAZ), nullValue(Object.class));
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) BAZ));
    }

    @Test
    public void testArgumentToMethod() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(ArgumentSample.class)
                .visit(MemberSubstitution.strict()
                        .field(named(FOO))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(ArgumentSample.class.getMethod("method", String.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) QUX));
        assertThat(type.getDeclaredMethod(RUN, String.class).invoke(instance, BAZ), nullValue(Object.class));
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) BAZ));
    }

    @Test
    public void testArgumentOptional() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(ArgumentSample.class)
                .visit(MemberSubstitution.strict()
                        .field(named(FOO))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(ArgumentSample.class.getMethod("optional", String.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) QUX));
        assertThat(type.getDeclaredMethod(RUN, String.class).invoke(instance, BAZ), nullValue(Object.class));
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), nullValue(Object.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testArgumentNone() throws Exception {
        new ByteBuddy()
                .redefine(ArgumentSample.class)
                .visit(MemberSubstitution.strict()
                        .field(named(FOO))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(ArgumentSample.class.getMethod("none", String.class)))
                        .on(named(RUN)))
                .make();
    }

    @Test
    public void testThisReferenceToElement() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(ThisReferenceSample.class)
                .visit(MemberSubstitution.strict()
                        .field(named(FOO))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(ThisReferenceSample.class.getMethod("element", Object.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance(), argument = type.getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) QUX));
        assertThat(type.getDeclaredMethod(RUN, type).invoke(instance, argument), nullValue(Object.class));
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is(argument));
    }

    @Test
    public void testThisReferenceToMethod() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(ThisReferenceSample.class)
                .visit(MemberSubstitution.strict()
                        .field(named(FOO))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(ThisReferenceSample.class.getMethod("method", Object.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance(), argument = type.getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) QUX));
        assertThat(type.getDeclaredMethod(RUN, type).invoke(instance, argument), nullValue(Object.class));
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is(instance));
    }

    @Test
    public void testThisReferenceOptional() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(ThisReferenceSample.class)
                .visit(MemberSubstitution.strict()
                        .field(named(BAZ))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(ThisReferenceSample.class.getMethod("optional", Object.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance(), argument = type.getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) QUX));
        assertThat(type.getDeclaredField(BAZ).get(null), is((Object) BAZ));
        assertThat(type.getDeclaredMethod(RUN, type).invoke(instance, argument), nullValue(Object.class));
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), nullValue(Object.class));
        assertThat(type.getDeclaredField(BAZ).get(null), is((Object) BAZ));
    }

    @Test(expected = IllegalStateException.class)
    public void testThisReferenceNone() throws Exception {
        new ByteBuddy()
                .redefine(ThisReferenceSample.class)
                .visit(MemberSubstitution.strict()
                        .field(named(BAZ))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(ThisReferenceSample.class.getMethod("none", Object.class)))
                        .on(named(RUN)))
                .make();
    }

    @Test
    public void testAllArgumentsToElement() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(AllArgumentsSample.class)
                .visit(MemberSubstitution.strict()
                        .field(named(FOO))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(AllArgumentsSample.class.getMethod("element", String[].class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) QUX));
        assertThat(type.getDeclaredMethod(RUN).invoke(instance), nullValue(Object.class));
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) BAR));
    }

    @Test
    public void testAllArgumentsToMethod() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(AllArgumentsSample.class)
                .visit(MemberSubstitution.strict()
                        .field(named(FOO))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(AllArgumentsSample.class.getMethod("method", String[].class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) QUX));
        assertThat(type.getDeclaredMethod(RUN, String.class).invoke(instance, BAZ), nullValue(Object.class));
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) BAZ));
    }

    @Test
    public void testAllArgumentsSelf() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(AllArgumentsSample.class)
                .visit(MemberSubstitution.strict()
                        .field(named(FOO))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(AllArgumentsSample.class.getMethod("self", Object[].class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) QUX));
        assertThat(type.getDeclaredField(BAZ).get(null), nullValue(Object.class));
        assertThat(type.getDeclaredMethod(RUN).invoke(instance), nullValue(Object.class));
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) BAR));
        assertThat(type.getDeclaredField(BAZ).get(null), is(instance));
    }

    @Test
    public void testAllArgumentsEmpty() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(AllArgumentsSample.class)
                .visit(MemberSubstitution.strict()
                        .field(named(BAR))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(AllArgumentsSample.class.getMethod("empty", String[].class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) QUX));
        assertThat(type.getDeclaredMethod(RUN).invoke(instance), nullValue(Object.class));
        assertThat(type.getDeclaredField(FOO).get(instance), nullValue(Object.class));
        assertThat(type.getDeclaredField(BAR).get(instance), is((Object) BAR));
        assertThat(type.getDeclaredField(QUX).get(null), is((Object) QUX));
    }

    @Test(expected = IllegalStateException.class)
    public void testAllArgumentsIllegal() throws Exception {
        new ByteBuddy()
                .redefine(ThisReferenceSample.class)
                .visit(MemberSubstitution.strict()
                        .field(named(BAZ))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(AllArgumentsSample.class.getMethod("illegal", Void.class)))
                        .on(named(RUN)))
                .make();
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, target = SelfCallHandleSample.class)
    public void testSelfCallHandle() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(SelfCallHandleSample.class)
                .visit(MemberSubstitution.strict()
                        .field(named(FOO))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(SelfCallHandleSample.class.getMethod("handle", Object.class, Object.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getDeclaredConstructor(String.class).newInstance(FOO);
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredMethod(RUN).invoke(instance), is((Object) (FOO + BAR)));
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, target = SelfCallHandleSample.class)
    public void testSelfCallHandleHierarchy() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(SelfCallHandleSample.class)
                .visit(MemberSubstitution.strict()
                        .field(named(FOO))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(SelfCallHandleSubclass.class.getMethod("handle", Object.class, Object.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER.opened())
                .getLoaded();
        Object instance = ((InjectionClassLoader) type.getClassLoader()).defineClass(SelfCallHandleSubclass.class.getName(), ClassFileLocator.ForClassLoader.read(SelfCallHandleSubclass.class))
                .getDeclaredConstructor(String.class)
                .newInstance(FOO);
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredMethod(RUN).invoke(instance), is((Object) (FOO + BAR)));
    }

    @Test
    public void testFieldValueNamedImplicit() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(FieldValueTest.class)
                .visit(MemberSubstitution.strict()
                        .field(named(BAR))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(FieldValueTest.class.getMethod("implicit", String.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getClassLoader().loadClass(FieldValueTest.class.getName()).getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredMethod(RUN).invoke(instance), is((Object) FOO));
    }

    @Test
    public void testFieldValueNamedExplicit() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(FieldValueTest.class)
                .visit(MemberSubstitution.strict()
                        .field(named(BAR))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(FieldValueTest.class.getMethod("explicit", String.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getClassLoader().loadClass(FieldValueTest.class.getName()).getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredMethod(RUN).invoke(instance), is((Object) FOO));
    }

    @Test
    public void testFieldValueAccessor() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(FieldValueTest.class)
                .visit(MemberSubstitution.strict()
                        .field(named(BAR))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(FieldValueTest.class.getMethod("accessor", String.class)))
                        .on(named("getFoo")))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getClassLoader().loadClass(FieldValueTest.class.getName()).getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredMethod("getFoo").invoke(instance), is((Object) FOO));
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, target = FieldGetterHandlerTest.class)
    public void testFieldGetterHandleNamedImplicit() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(FieldGetterHandlerTest.class)
                .visit(MemberSubstitution.strict()
                        .field(named(BAR))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(FieldGetterHandlerTest.class.getMethod("implicit", Object.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getClassLoader().loadClass(FieldGetterHandlerTest.class.getName()).getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredMethod(RUN).invoke(instance), is((Object) FOO));
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, target = FieldGetterHandlerTest.class)
    public void testFieldGetterHandleNamedExplicit() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(FieldGetterHandlerTest.class)
                .visit(MemberSubstitution.strict()
                        .field(named(BAR))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(FieldGetterHandlerTest.class.getMethod("explicit", Object.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getClassLoader().loadClass(FieldGetterHandlerTest.class.getName()).getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredMethod(RUN).invoke(instance), is((Object) FOO));
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, target = FieldGetterHandlerTest.class)
    public void testFieldGetterHandleAccessor() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(FieldGetterHandlerTest.class)
                .visit(MemberSubstitution.strict()
                        .field(named(BAR))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(FieldGetterHandlerTest.class.getMethod("accessor", Object.class)))
                        .on(named("getFoo")))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getClassLoader().loadClass(FieldGetterHandlerTest.class.getName()).getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredMethod("getFoo").invoke(instance), is((Object) FOO));
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, target = FieldSetterHandlerTest.class)
    public void testFieldSetterHandleNamedImplicit() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(FieldSetterHandlerTest.class)
                .visit(MemberSubstitution.strict()
                        .field(named(BAR))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(FieldSetterHandlerTest.class.getMethod("implicit", Object.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getClassLoader().loadClass(FieldSetterHandlerTest.class.getName()).getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredMethod(RUN).invoke(instance), is((Object) BAZ));
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) QUX));
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, target = FieldSetterHandlerTest.class)
    public void testFieldSetterHandleNamedExplicit() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(FieldSetterHandlerTest.class)
                .visit(MemberSubstitution.strict()
                        .field(named(BAR))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(FieldSetterHandlerTest.class.getMethod("explicit", Object.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getClassLoader().loadClass(FieldSetterHandlerTest.class.getName()).getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredMethod(RUN).invoke(instance), is((Object) BAZ));
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) QUX));
    }

    @Test
    @JavaVersionRule.Enforce(value = 7, target = FieldSetterHandlerTest.class)
    public void testFieldSetterHandleAccessor() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(FieldSetterHandlerTest.class)
                .visit(MemberSubstitution.strict()
                        .field(named(BAR))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(FieldSetterHandlerTest.class.getMethod("accessor", Object.class)))
                        .on(named("getFoo")))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getClassLoader().loadClass(FieldSetterHandlerTest.class.getName()).getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredMethod("getFoo").invoke(instance), is((Object) BAZ));
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) QUX));
    }

    @Test
    public void testUnused() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(UnusedTest.class)
                .visit(MemberSubstitution.strict()
                        .field(named(FOO))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(UnusedTest.class.getMethod("unused", Object.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getClassLoader().loadClass(UnusedTest.class.getName()).getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredMethod(RUN).invoke(instance), is((Object) BAR));
    }

    @Test
    public void testStubValue() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(StubValueTest.class)
                .visit(MemberSubstitution.strict()
                        .field(named(FOO))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(StubValueTest.class.getMethod("stubbed", Object.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getClassLoader().loadClass(StubValueTest.class.getName()).getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredMethod(RUN).invoke(instance), is((Object) BAR));
    }

    @Test(expected = IllegalStateException.class)
    public void testStubValueIllegal() throws Exception {
        new ByteBuddy()
                .redefine(StubValueTest.class)
                .visit(MemberSubstitution.strict()
                        .field(named(FOO))
                        .replaceWithChain(MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(StubValueTest.class.getMethod("illegal", String.class)))
                        .on(named(RUN)))
                .make();
    }

    @Test
    public void testCurrent() throws Exception {
        Class<?> type = new ByteBuddy()
                .redefine(CurrentTest.class)
                .visit(MemberSubstitution.strict()
                        .field(named(FOO))
                        .replaceWithChain(
                                MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(CurrentTest.class.getMethod("first", String.class)),
                                MemberSubstitution.Substitution.Chain.Step.ForDelegation.to(CurrentTest.class.getMethod("second", String.class)))
                        .on(named(RUN)))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object instance = type.getClassLoader().loadClass(CurrentTest.class.getName()).getDeclaredConstructor().newInstance();
        assertThat(type.getDeclaredField(FOO).get(instance), is((Object) FOO));
        assertThat(type.getDeclaredMethod(RUN).invoke(instance), is((Object) QUX));
    }

    public static class ArgumentSample {

        public String foo = FOO, bar = BAR;

        public static String qux = QUX;

        @SuppressWarnings("unused")
        public void run(String value) {
            foo = value;
        }

        public static void element(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.Argument(0) String value) {
            qux = value;
        }

        public static void method(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.Argument(value = 0, source = MemberSubstitution.Substitution.Chain.Step.ForDelegation.Source.ENCLOSING_METHOD) String value) {
            qux = value;
        }

        public static void optional(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.Argument(value = 1, optional = true) String value) {
            qux = value;
        }

        public static void none(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.Argument(value = 1) String value) {
            qux = value;
        }
    }

    public static class ThisReferenceSample {

        public Object foo = FOO, bar = BAR;

        public static Object qux = QUX, baz = BAZ;

        @SuppressWarnings("unused")
        public void run(ThisReferenceSample sample) {
            sample.foo = sample.bar;
            baz = sample.bar;
        }

        public static void element(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.This Object value) {
            qux = value;
        }

        public static void method(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.This(source = MemberSubstitution.Substitution.Chain.Step.ForDelegation.Source.ENCLOSING_METHOD) Object value) {
            qux = value;
        }

        public static void optional(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.This(optional = true) Object value) {
            qux = value;
        }

        public static void none(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.This Object value) {
            qux = value;
        }
    }

    public static class AllArgumentsSample {

        public String foo = FOO, bar = BAR;

        public static String qux = QUX;
        public static Object baz;

        @SuppressWarnings("unused")
        public void run(String value) {
            foo = value;
        }

        public void run() {
            foo = bar;
        }

        public static void element(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.AllArguments String[] value) {
            if (value.length != 1) {
                throw new AssertionError();
            }
            qux = value[0];
        }

        public static String empty(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.AllArguments(nullIfEmpty = true) String[] value) {
            if (value != null) {
                throw new AssertionError();
            }
            return null;
        }

        public static void method(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.AllArguments(source = MemberSubstitution.Substitution.Chain.Step.ForDelegation.Source.ENCLOSING_METHOD) String[] value) {
            if (value.length != 1) {
                throw new AssertionError();
            }
            qux = value[0];
        }

        public static void self(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.AllArguments(includeSelf = true) Object[] value) {
            if (value.length != 2) {
                throw new AssertionError();
            }
            qux = (String) value[1];
            baz = value[0];
        }

        public static void illegal(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.AllArguments Void ignored) {
            throw new AssertionError();
        }
    }

    public static class SelfCallHandleSample {

        public final String foo;

        public SelfCallHandleSample(String foo) {
            this.foo = foo;
        }

        public String run() {
            return foo;
        }

        public static String handle(
                @MemberSubstitution.Substitution.Chain.Step.ForDelegation.SelfCallHandle Object bound,
                @MemberSubstitution.Substitution.Chain.Step.ForDelegation.SelfCallHandle(bound = false) Object unbound) throws Throwable {
            Method method = Class.forName("java.lang.invoke.MethodHandle").getMethod("invokeWithArguments", List.class);
            return method.invoke(bound, Collections.emptyList()).toString() + method.invoke(unbound, Collections.singletonList(new SelfCallHandleSample(BAR)));
        }
    }

    public static class SelfCallHandleSubclass extends SelfCallHandleSample {

        private int check;

        public SelfCallHandleSubclass(String foo) {
            super(foo);
        }

        @Override
        public String run() {
            if (check++ != 0) {
                throw new AssertionError();
            }
            return super.run();
        }

        public static String handle(
                @MemberSubstitution.Substitution.Chain.Step.ForDelegation.SelfCallHandle Object bound,
                @MemberSubstitution.Substitution.Chain.Step.ForDelegation.SelfCallHandle(bound = false) Object unbound) throws Throwable {
            Method method = Class.forName("java.lang.invoke.MethodHandle").getMethod("invokeWithArguments", List.class);
            return method.invoke(bound, Collections.emptyList()).toString() + method.invoke(unbound, Collections.singletonList(new SelfCallHandleSubclass(BAR)));
        }
    }

    public static class FieldValueTest {

        public String foo = FOO, bar = BAR;

        public String run() {
            return bar;
        }

        public String getFoo() {
            return bar;
        }

        public String implicit(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.FieldValue(FOO) String value) {
            return value;
        }

        public String accessor(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.FieldValue String value) {
            return value;
        }

        public String explicit(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.FieldValue(value = FOO, declaringType = FieldValueTest.class) String value) {
            return value;
        }
    }

    public static class FieldGetterHandlerTest {

        public String foo = FOO, bar = BAR;

        public String run() {
            return bar;
        }

        public String getFoo() {
            return bar;
        }

        public String implicit(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.FieldGetterHandle(FOO) Object handle) throws Throwable {
            Method method = Class.forName("java.lang.invoke.MethodHandle").getMethod("invokeWithArguments", List.class);
            return (String) method.invoke(handle, Collections.emptyList());
        }

        public String accessor(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.FieldGetterHandle Object handle) throws Throwable {
            Method method = Class.forName("java.lang.invoke.MethodHandle").getMethod("invokeWithArguments", List.class);
            return (String) method.invoke(handle, Collections.emptyList());
        }

        public String explicit(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.FieldGetterHandle(value = FOO, declaringType = FieldGetterHandlerTest.class) Object handle) throws Throwable {
            Method method = Class.forName("java.lang.invoke.MethodHandle").getMethod("invokeWithArguments", List.class);
            return (String) method.invoke(handle, Collections.emptyList());
        }
    }

    public static class FieldSetterHandlerTest {

        public String foo = FOO, bar = BAR;

        public String run() {
            return bar;
        }

        public String getFoo() {
            return bar;
        }

        public String implicit(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.FieldSetterHandle(FOO) Object handle) throws Throwable {
            Method method = Class.forName("java.lang.invoke.MethodHandle").getMethod("invokeWithArguments", List.class);
            method.invoke(handle, Collections.singletonList(QUX));
            return BAZ;
        }

        public String accessor(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.FieldSetterHandle Object handle) throws Throwable {
            Method method = Class.forName("java.lang.invoke.MethodHandle").getMethod("invokeWithArguments", List.class);
            method.invoke(handle, Collections.singletonList(QUX));
            return BAZ;
        }

        public String explicit(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.FieldSetterHandle(value = FOO, declaringType = FieldSetterHandlerTest.class) Object handle) throws Throwable {
            Method method = Class.forName("java.lang.invoke.MethodHandle").getMethod("invokeWithArguments", List.class);
            method.invoke(handle, Collections.singletonList(QUX));
            return BAZ;
        }
    }

    public static class UnusedTest {

        public String foo = FOO;

        public String run() {
            return foo;
        }

        public String unused(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.Unused Object value) {
            if (value != null) {
                throw new AssertionError();
            }
            return BAR;
        }
    }

    public static class StubValueTest {

        public String foo = FOO;

        public String run() {
            return foo;
        }

        public String stubbed(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.StubValue Object value) {
            if (value != null) {
                throw new AssertionError();
            }
            return BAR;
        }

        public String illegal(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.StubValue String value) {
            throw new AssertionError();
        }
    }

    public static class CurrentTest {

        public String foo = FOO;

        public String run() {
            return foo;
        }

        public String first(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.Current String value) {
            if (value != null) {
                throw new AssertionError();
            }
            return BAR;
        }

        public String second(@MemberSubstitution.Substitution.Chain.Step.ForDelegation.Current String value) {
            if (!value.equals(BAR)) {
                throw new AssertionError();
            }
            return QUX;
        }
    }
}