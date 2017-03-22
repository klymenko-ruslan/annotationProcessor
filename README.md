# annotationProcessor

 Simple implementation of custom annotation processor which validates all types annotated with @Bean annotation using such rules:
1) Bean should implement serializable interface.
2) Bean should override equals, hashCode and toString methods.
3) Bean's fields should be private and implement getters/setters.
