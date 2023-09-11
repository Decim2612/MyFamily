package com.example.myfamily

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfamily.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*


class HomeFragment : Fragment() {
    lateinit var inviteAdapter :InviteAdapter
    lateinit var mContext: Context
    private val listContacts: ArrayList<ContactModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listMembers = listOf<MemberModel>(
            MemberModel(
                "Om Katiyar",
                "9th buildind, 2nd floor, maldiv road",
                "220",
                "90%"
            ),
            MemberModel(
                "Ayush",
                "9th buildind, 3rd floor, maldiv road",
                "170",
                "80%"
            ),
            MemberModel(
                "Yash",
                "9th buildind, 4th floor, maldiv road",
                "180",
                "70%"
            ),
            MemberModel(
                "Aashu",
                "9th buildind, 5th floor, maldiv road",
                "9",
                "78%"
            ),
            MemberModel(
                "Manan", "9th buildind, 3rd floor, maldiv road",
                "170",
                "80%"
            ),

            )
        val adapter = MemberAdapter(listMembers)

        binding.recyclerMember.layoutManager = LinearLayoutManager(mContext)
        binding.recyclerMember.adapter = adapter





         inviteAdapter = InviteAdapter(listContacts)
        fetchDatabaseContacts()
        CoroutineScope(Dispatchers.IO).launch {




            insertDatabaseContacts(fetchContacts())


        }



        binding.recyclerInvite.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerInvite.adapter = inviteAdapter



        binding.threeDots.setOnClickListener{
            SharedPref.putBoolean(PrefConstants.IS_USER_LOGGED_IN,false)
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "Sign out", Toast.LENGTH_SHORT).show()
        }


    }

    private fun fetchDatabaseContacts() {
        val database=MyFamilyDataBase.getDataBase(mContext)

         database.contactDao().getAllContacts().observe(viewLifecycleOwner){
             listContacts.clear()
             listContacts.addAll(it)
             inviteAdapter.notifyDataSetChanged()
         }
    }




    @SuppressLint("SuspiciousIndentation")
    private suspend fun insertDatabaseContacts(listContacts: ArrayList<ContactModel>) {
        val database = MyFamilyDataBase.getDataBase(mContext)

            database.contactDao().insertAll(listContacts)



    }






    @SuppressLint("Range")
    private fun fetchContacts(): ArrayList<ContactModel> {
        val cr = requireActivity().contentResolver
        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        val listContacts: ArrayList<ContactModel> = ArrayList()
        if (cursor != null && cursor.count > 0) {
            while (cursor != null && cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber =
                    cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if (hasPhoneNumber > 0) {
                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        ""
                    )
                    if (pCur != null && pCur.count > 0) {
                        while (pCur != null && pCur.moveToNext()) {
                            val phoneNum =
                                pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                            listContacts.add(ContactModel(name, phoneNum))
                        }
                        pCur.close()
                    }

                }
            }
            if (cursor != null) {
                cursor.close()
            }
        }
        return listContacts
    }

    companion object {

        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}