package org.mixit.conference.ui.security

import kotlinx.html.*
import org.mixit.conference.model.people.Email
import org.mixit.conference.model.shared.Context
import org.mixit.conference.ui.component.*
import org.mixit.conference.ui.form.FormDescriptor
import org.mixit.conference.ui.form.FormField
import org.mixit.conference.ui.form.FormFieldType
import org.mixit.conference.ui.renderTemplate

fun loginStartForm(
    email: Email? = null,
    dirty: Boolean = false,
    valuesInRequest: Map<String, String?> = emptyMap(),
    converter: (FormDescriptor<Email>) -> Email = { throw IllegalStateException() }
) = FormDescriptor(
    fields = listOf(
        FormField(
            "email",
            type = FormFieldType.Email,
            fieldLabel = "Email",
            dirty = dirty,
            isRequired = true,
            defaultValue = email ?: valuesInRequest["email"]
        )
    ),
    converter = converter
)

fun renderLoginStartPage(context: Context, formValue: FormDescriptor<Email>) =
    renderTemplate(context) {
        authLayout(context, context.i18n("login.title"), context.i18n("login.v2.email")) {
            formValue.form(this, action = "/login") {
                button(classes = "mxt-button mxt-button--primary mxt-auth__submit", type = ButtonType.submit) {
                    +context.i18n("login.action.token")
                }
            }
        }
    }
