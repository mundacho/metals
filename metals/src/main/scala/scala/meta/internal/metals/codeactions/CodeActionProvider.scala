package scala.meta.internal.metals.codeactions

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import scala.meta.internal.metals._
import scala.meta.internal.metals.clients.language.MetalsLanguageClient
import scala.meta.internal.metals.codeactions.CodeAction
import scala.meta.internal.parsing.Trees
import scala.meta.pc.CancelToken

import org.eclipse.{lsp4j => l}

final class CodeActionProvider(
    compilers: Compilers,
    buffers: Buffers,
    buildTargets: BuildTargets,
    scalafixProvider: ScalafixProvider,
    trees: Trees,
    diagnostics: Diagnostics,
    languageClient: MetalsLanguageClient,
)(implicit ec: ExecutionContext) {

  private val extractMemberAction = new ExtractRenameMember(trees)

  private val allActions: List[CodeAction] = List(
    new ImplementAbstractMembers(compilers),
    new ImportMissingSymbol(compilers, buildTargets),
    new CreateNewSymbol(),
    new StringActions(buffers),
    extractMemberAction,
    new SourceOrganizeImports(
      scalafixProvider,
      buildTargets,
      diagnostics,
      languageClient,
    ),
    new OrganizeImportsQuickFix(
      scalafixProvider,
      buildTargets,
      diagnostics,
    ),
    new InsertInferredType(trees),
    new PatternMatchRefactor(trees),
    new RewriteBracesParensCodeAction(trees),
    new ExtractValueCodeAction(trees, buffers),
    new CreateCompanionObjectCodeAction(trees, buffers),
    new ConvertToNamedArguments(trees),
    new FlatMapToForComprehensionCodeAction(trees, buffers),
  )

  def codeActions(
      params: l.CodeActionParams,
      token: CancelToken,
  )(implicit ec: ExecutionContext): Future[Seq[l.CodeAction]] = {
    val requestedKinds = Option(params.getContext.getOnly).map(_.asScala.toList)

    def isRequestedKind(action: CodeAction): Boolean =
      requestedKinds match {
        case Some(only) =>
          only.exists(requestedKind => action.kind.startsWith(requestedKind))
        case None => true
      }

    val actions = allActions.collect {
      case action if isRequestedKind(action) => action.contribute(params, token)
    }

    Future.sequence(actions).map(_.flatten)
  }

  def executeCommands(
      codeActionCommandData: CodeActionCommandData
  ): Future[CodeActionCommandResult] = {
    codeActionCommandData match {
      case data: ExtractMemberDefinitionData =>
        extractMemberAction.executeCommand(data)
      case data => Future.failed(new IllegalArgumentException(data.toString))
    }
  }
}
