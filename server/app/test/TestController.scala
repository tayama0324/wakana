package test

import javax.inject.Inject

import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Results

/**
 * Created by takashi_tayama on 2015/12/30.
 */
class TestController @Inject() extends Controller{

  def assets(path: String, file: String): Action[AnyContent] = Action { _ =>
    Results.Ok(s"Path = $path\nFile = $file")
  }

  def get(): Action[AnyContent] = Action { request =>
    Results.Ok(views.html.index("Title"))
  }
}
