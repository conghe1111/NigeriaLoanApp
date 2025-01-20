package com.chocolate.nigerialoanapp.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.base.BaseFragment
import com.chocolate.nigerialoanapp.widget.InfoEditView
import com.chocolate.nigerialoanapp.widget.InfoSelectView

class EditBasicInfoFragment : BaseFragment() {

    private var editFirstName : InfoEditView? = null
    private var editMiddleName : InfoEditView? = null
    private var editLastName : InfoEditView? = null
    private var editBvn : InfoEditView? = null

    private var selectBirth : InfoSelectView? = null
    private var selectGender : InfoSelectView? = null
    private var selectMarital : InfoSelectView? = null
    private var selectEducation : InfoSelectView? = null

    private var editEmail : InfoEditView? = null

    private var selectAddress : InfoSelectView? = null
    private var editStreet : InfoEditView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_basic_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editFirstName = view.findViewById<InfoEditView>(R.id.edit_first_name)
        editMiddleName = view.findViewById<InfoEditView>(R.id.edit_middle_name)
        editLastName = view.findViewById<InfoEditView>(R.id.edit_last_name)
        editBvn = view.findViewById<InfoEditView>(R.id.edit_bvn)
        selectBirth = view.findViewById<InfoSelectView>(R.id.select_birth)
        selectGender = view.findViewById<InfoSelectView>(R.id.select_gender)
        selectMarital = view.findViewById<InfoSelectView>(R.id.select_marital)
        selectEducation = view.findViewById<InfoSelectView>(R.id.select_education)
        editEmail = view.findViewById<InfoEditView>(R.id.edit_email)
        selectAddress = view.findViewById<InfoSelectView>(R.id.select_address)
        editStreet = view.findViewById<InfoEditView>(R.id.edit_street)
    }
}