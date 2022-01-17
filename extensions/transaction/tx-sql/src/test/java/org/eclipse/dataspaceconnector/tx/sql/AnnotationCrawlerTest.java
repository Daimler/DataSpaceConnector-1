package org.eclipse.dataspaceconnector.tx.sql;

import org.eclipse.dataspaceconnector.transaction.tx.Transactional;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

public class AnnotationCrawlerTest {


    @Test
    public void myTest() {

        MyClass m = new MyClass(
                new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(
                        new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(
                                new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(
                                        new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(
                                                new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(
                                                        new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(new Wrapper(
                                                                new End(null)
                                                        )))))))))
                                                )))))))))
                                        )))))))))
                                )))))))))
                        )))))))))
                )))))))))
        );

        m.call();
    }

    private void findKey() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            Class c = ste.getClass();

            Annotation[] annotations = c.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                System.out.println("ANNOTATION: " + annotation.toString());
                if (annotation instanceof Transactional) {
                    System.out.println("KEY " + ((Transactional) annotation).key());
                }
            }

        }
    }

    private static class Wrapper {
        private final Wrapper w;

        private Wrapper(Wrapper w) {
            this.w = w;
        }

        public void call() {
            System.out.println("WRAPPED");
            w.call();
        }
    }


    @Transactional(key = "test")
    private static class MyClass extends Wrapper {
        private MyClass(Wrapper w) {
            super(w);
        }

        @Override
        public void call() {
            System.out.println("MYCLASS");
            super.call();
        }
    }

    private static class End extends Wrapper {
        private End(Wrapper w) {
            super(w);
        }

        @Override
        public void call() {

            System.out.println("END");

            StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
            for (int i = 1; i < stElements.length; i++) {
                StackTraceElement ste = stElements[i];

                if (!ste.getClassName().startsWith("org.eclipse.dataspaceconnector")) {
                    continue;
                }

                Class c = null;
                try {
                    c = Class.forName(ste.getClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                Annotation[] annotations = c.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    System.out.println("ANNOTATION: " + annotation.toString());
                }

            }
        }
    }
}
