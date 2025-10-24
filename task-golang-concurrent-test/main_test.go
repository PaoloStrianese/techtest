package main

import (
	"fmt"
	"sync"
	"testing"
)

func TestCounter(t *testing.T) {

	t.Run("processing 3 times brings the counter to 3", func(t *testing.T) {
		mw := &MeasuredWorker{Worker: &SlowWorker{}}

		fmt.Println("First call to Work()")
		t.Logf("DEBUG: about to call Work #1")
		mw.Work()
		fmt.Printf("Counter after first Work: %d\n", mw.Value())
		t.Logf("DEBUG: after Work #1 value=%d", mw.Value())

		fmt.Println("Second call to Work()")
		t.Logf("DEBUG: about to call Work #2")
		mw.Work()
		fmt.Printf("Counter after second Work: %d\n", mw.Value())
		t.Logf("DEBUG: after Work #2 value=%d", mw.Value())

		fmt.Println("Third call to Work()")
		t.Logf("DEBUG: about to call Work #3")
		mw.Work()
		fmt.Printf("Counter after third Work: %d\n", mw.Value())
		t.Logf("DEBUG: after Work #3 value=%d", mw.Value())

		assertEqual(t, mw.Value(), 3)
		fmt.Println("=== Sequential test completed ===")
	})

	t.Run("concurrent processing and counting", func(t *testing.T) {
		var wantedCount int32 = 1000
		mw := &MeasuredWorker{Worker: &SlowWorker{}}

		t.Logf("DEBUG: launching %d goroutines", wantedCount)
		var wg sync.WaitGroup
		wg.Add(int(wantedCount))

		for i := 0; i < int(wantedCount); i++ {
			if i%100 == 0 {
				t.Logf("DEBUG: launching goroutine %d", i)
			}
			go func(id int) {
				if id%100 == 0 {
					t.Logf("DEBUG: goroutine %d starting", id)
				}
				mw.Work()
				if id%100 == 0 {
					t.Logf("DEBUG: goroutine %d finished", id)
				}
				wg.Done()
			}(i)
		}
		wg.Wait()

		gotCount := mw.Value()
		t.Logf("DEBUG: final count observed = %d", gotCount)
		assertEqual(t, gotCount, wantedCount)
	})

}

func assertEqual(t testing.TB, got int32, want int32) {
	t.Helper()
	if got != want {
		t.Errorf("got %d, want %d", got, want)
	}
}
