/*
 * MIT License
 *
 * Copyright (c) 2021 Fabricio Batista Narcizo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dk.itu.moapd.threads

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dk.itu.moapd.threads.databinding.FragmentHandlerBinding
import java.util.concurrent.Executors

/**
 * A fragment to show the `HandlerFragment` and execute a work thread that updates a progress bar.
 *
 * The `MainActivity` has a `FrameLayout` area to replace dynamically the fragments used by this
 * project. You can use a bundle to share data between the main activity and this fragment.
 */
class HandlerFragment : Fragment() {

    /**
     * The current value showing in the progress bar.
     */
    private var cont = 100

    /**
     * A flag to control the start/stop button in the application.
     */
    private var status = false

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private var _binding: FragmentHandlerBinding? = null

    /**
     * This property is only valid between `onCreateView()` and `onDestroyView()` methods.
     */
    private val binding get() = _binding!!

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and
     * non-graphical fragments can return null. This will be called between `onCreate(Bundle)` and
     * `onViewCreated(View, Bundle)`. A default `View` can be returned by calling `Fragment(int)` in
     * your constructor. Otherwise, this method returns null.
     *
     * It is recommended to <strong>only</strong> inflate the layout in this method and move logic
     * that operates on the returned View to `onViewCreated(View, Bundle)`.
     *
     * If you return a `View` from here, you will later be called in `onDestroyView()` when the view
     * is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the
     *      fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be
     *      attached to. The fragment should not add the view itself, but this can be used to
     *      generate the `LayoutParams` of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *      saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHandlerBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called when the view previously created by `onCreateView()` has been detached from the
     * fragment. The next time the fragment needs to be displayed, a new view will be created. This
     * is called after `onStop()` and before `onDestroy()`. It is called <em>regardless</em> of
     * whether `onCreateView()` returned a non-null view. Internally it is called after the view's
     * state has been saved but before it has been removed from its parent.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Called immediately after `onCreateView(LayoutInflater, ViewGroup, Bundle)` has returned, but
     * before any saved state has been restored in to the view. This gives subclasses a chance to
     * initialize themselves once they know their view hierarchy has been completely created. The
     * fragment's view hierarchy is not however attached to its parent at this point.
     *
     * @param view The View returned by `onCreateView(LayoutInflater, ViewGroup, Bundle)`.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *      saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Define the UI components behavior.
        with (binding) {

            // Reset button.
            resetButton.setOnClickListener {
                cont = 0
            }

            // Start/Stop button.
            startButton.setOnClickListener {
                status = !status
                if (status) {
                    cont = 0
                    startButton.text = getString(R.string.stop_button)
                    Executors.newSingleThreadExecutor().execute {
                        Thread(HandlerTask()).start()
                    }
                } else
                    startButton.text = getString(R.string.start_button)

                resetButton.isEnabled = status
            }

            // The initial value in the progress bar.
            progressBar.progress = cont
        }
    }

    /**
     * A internal class to manager a worker thread to execute an Android task in background.
     */
    private inner class HandlerTask : Runnable {

        /**
         * When an object implementing interface `Runnable` is used to create a thread, starting the
         * thread causes the object's `run()` method to be called in that separately executing
         * thread.
         *
         * The general contract of the method `run()` is that it may take any action whatsoever.
         */
        override fun run() {

            // Run this block until the user presses the stop button.
            while (status) {

                // Stops the worker thread for 100 milliseconds.
                try {
                    Thread.sleep(250)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                // Send a post to update the progress bar in the UI thread.
                Handler(Looper.getMainLooper()).post {
                    binding.progressBar.progress = cont++
                    cont %= 100
                }
            }
        }
    }

}
