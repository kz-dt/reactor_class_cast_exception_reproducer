package org.example;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.function.Function;

// Causes java.lang.ClassCastException:
// 		class reactor.core.publisher.FluxContextWriteRestoringThreadLocals$ContextWriteRestoringThreadLocalsSubscriber
// 		cannot be cast to class reactor.core.Fuseable$QueueSubscription
public class ReactorClassCastExceptionReproducerSample {
	public static void main(String[] args) {
		new ReactorClassCastExceptionReproducerSample().runSample();
	}

	private <T> Function<? super Publisher<T>, ? extends Publisher<T>> tracingLift() {
		return Operators.lift((a, b) -> b);
	}

	private void runSample() {
		// Setting noOp lifter, without it scenario passes
		Hooks.onEachOperator("testTracingLift", tracingLift());
		Hooks.enableAutomaticContextPropagation();

		// Uncomment this to make scenario pass
		// onOperatorDebug wraps lift with MonoOnAssembly and everything is fine
		// Hooks.onOperatorDebug();

		Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
		Flux<String> flux = sink
			.asFlux()
			.doOnRequest(v -> System.out.println("OnDoRequest " + v))
			.doOnTerminate(() -> System.out.println("doOnTerminate"))
			.doOnCancel(() -> System.out.println("doOnCancel"))
			.publish()
			.refCount();

		Mono<List<String>> res = flux.map(s -> s + " mapped").collectList();
		res.subscribe(v -> System.out.println("Received a list of mapped strings: " + v));

		System.out.println("Scenario passed");
	}

}
