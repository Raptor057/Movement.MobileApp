import android.text.SpannableString
import android.text.util.Linkify
import androidx.lifecycle.*

class HelpViewModel : ViewModel() {
    private fun buildHelpText(): CharSequence {
        val s = SpannableString("Technical support\nsistemas@generaltransmissions.com")
        Linkify.addLinks(s, Linkify.EMAIL_ADDRESSES)
        return s
    }

    private val _text = MutableLiveData<CharSequence>(buildHelpText())
    val text: LiveData<CharSequence> = _text
}
