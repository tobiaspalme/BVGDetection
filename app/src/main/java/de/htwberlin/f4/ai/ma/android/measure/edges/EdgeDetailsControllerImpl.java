package de.htwberlin.f4.ai.ma.android.measure.edges;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.carol.bvg.R;

import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;

/**
 * EdgeDetailsControllerImpl Class which implements the EdgeDetailsController Interface
 *
 * Used for managing edge details
 *
 * Author: Benjamin Kneer
 */

public class EdgeDetailsControllerImpl implements EdgeDetailsController {

    private EdgeDetailsView view;

    private Node startNode;
    private Node targetNode;
    private Edge edge;


    /************************************************************************************
    *                                                                                   *
    *                               Interface Methods                                   *
    *                                                                                   *
    *************************************************************************************/


    /**
     * set the responsible view
     *
     * @param view EdgeDetailsView
     */
    @Override
    public void setView(EdgeDetailsView view) {
        this.view = view;
    }


    /**
     * set start and target node.
     * The nodes will be retrieved from database using the ids
     *
     * @param startNodeId id of the start node
     * @param targetNodeId id of the target node
     */
    @Override
    public void setNodes(String startNodeId, String targetNodeId) {
        DatabaseHandler databaseHandler = DatabaseHandlerFactory.getInstance(view.getContext());
        startNode = databaseHandler.getNode(startNodeId);
        targetNode = databaseHandler.getNode(targetNodeId);
    }


    /**
     * triggered by clicking on delete button
     *
     * Show Dialog to make sure the user wants to delete the edge
     */
    @Override
    public void onDeleteClicked() {
        if (edge != null) {
            // ask the user if he really wants to delete the edge
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
            alertDialogBuilder.setTitle("Weg löschen");
            alertDialogBuilder.setMessage("Sind Sie sicher, dass Sie den Weg löschen möchten?");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setIcon(R.drawable.trash);

            // yes, user wants to delete the edge
            alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    deleteEdge();
                    dialog.dismiss();
                    view.finish();
                }
            });

            // no, user doesn't want to delete the edge
            alertDialogBuilder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }


    /**
     * triggered by clicking on save button
     *
     * Save the changed Edge into database and finish the activity
     */
    @Override
    public void onSaveClicked() {
        if (edge != null) {
            DatabaseHandler databaseHandler = DatabaseHandlerFactory.getInstance(view.getContext());
            databaseHandler.updateEdge(edge);
            view.finish();
        }
    }


    /**
     * triggered by changing handycap status of the edge
     *
     * @param handycapFriendly handycap friendly / not handycap friendly
     */
    @Override
    public void onHandycapChanged(boolean handycapFriendly) {
        if (edge != null) {
            edge.setAccessibility(handycapFriendly);
        }
    }


    /**
     * triggered by entering an info for the edge
     *
     * @param info edge description
     */
    @Override
    public void onEdgeInfoChanged(String info) {
        if (edge != null) {
            edge.setAdditionalInfo(info);
        }
    }


    /**
     * activity event
     *
     * update view and load edge from database
     */
    @Override
    public void onResume() {
        // make sure we have a valid start and target node
        if (startNode != null && targetNode != null) {
            // update view with node infos
            view.updateStartNodeInfo(startNode);
            view.updateTargetNodeInfo(targetNode);
            // get edge from database
            DatabaseHandler databaseHandler = DatabaseHandlerFactory.getInstance(view.getContext());
            edge = databaseHandler.getEdge(startNode, targetNode);
            // update view with edge info
            view.updateEdgeInfo(edge);
        }
    }


    /************************************************************************************
    *                                                                                   *
    *                               Class Methods                                       *
    *                                                                                   *
    *************************************************************************************/


    /**
     * delete the current edge from database
     */
    private void deleteEdge() {
        if (edge != null) {
            DatabaseHandler databaseHandler = DatabaseHandlerFactory.getInstance(view.getContext());
            databaseHandler.deleteEdge(edge);
        }
    }
}
