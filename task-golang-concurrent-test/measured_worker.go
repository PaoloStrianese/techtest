package main

import "sync/atomic"

type MeasuredWorker struct {
	Worker
	value int32
}

func (m *MeasuredWorker) Work() {

	//Solution final count
	atomic.AddInt32(&m.value, 1)
	// I chose an atomic call to increment the counter
	// so I avoid using a mutual exclusion solution, because it blocks the entire critical section
	// while with an atomic operation I only have operations on indivisible primitive variables

	//Solution long execution time
	go m.Worker.Work()
	//This approach aims to solve the latency problem,
	//however, in my opinion, it is not an ideal solution, as it could cause non-deterministic behavior,
	//since the encapsulated worker's Work() call is executed in a separate goroutine
	//and therefore is not guaranteed to complete beforethe MeasuredWorker's Work() function finishes. 
	//This approach could cause a deadlock if they try to access already released resources, or even scenarios where the goroutine fails to finish on repeated tests.
}

func (m *MeasuredWorker) Value() int32 {
	return atomic.LoadInt32(&m.value)
}
