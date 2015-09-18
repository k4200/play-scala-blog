package controllers

import javax.inject.Inject

import dal.UserRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class LoginController @Inject() (repo: UserRepository, val messagesApi: MessagesApi) (implicit ec: ExecutionContext) extends Controller with I18nSupport{
  val loginUser: Form[LoginUserForm] = Form {
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginUserForm.apply)(LoginUserForm.unapply)
  }

  def index = Action {
    Ok(views.html.index(loginUser))
  }

  def login = Action.async { implicit request =>
    loginUser.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.index(errorForm)))
      },
      user => {
        println("login!!!.")
        Future.successful(Ok(views.html.board("Login complete!")))
        if (repo.find(user.email, user.password)) {
          Future.successful(Ok(views.html.board("Login complete!")))
        } else {
          val errorMessage = "email or password is invalid."
          val formWithErrors = loginUser.withError("email", errorMessage).withError("password", errorMessage)
          Future.successful(Ok(views.html.index(formWithErrors)))
        }
      }
    )
  }
}

case class LoginUserForm(email: String, password: String)
